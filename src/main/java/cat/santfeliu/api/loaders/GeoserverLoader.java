package cat.santfeliu.api.loaders;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;

import cat.santfeliu.api.beans.LoaderJsonObject;
import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.enumerator.GeoserverLoaderConfigKeys;
import cat.santfeliu.api.enumerator.GlobalLoaderConfigKeys;
import cat.santfeliu.api.exceptions.ApiErrorException;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.utils.QuickSortAlgorithm;

public class GeoserverLoader extends ConnectorLoader {

	private static final Logger log = LoggerFactory.getLogger(GeoserverLoader.class);
	
	@Override
	public JsonObject load(long timeout) {
		if (loaded == null && this.reset) {
			this.checkedDeletions = false;
			this.reset = false;
			log.debug("load@GeoserverLoader - load response of timeout {}",timeout);
			return loadResponse(timeout);
		} else {
			if (this.checkedDeletions) {
				log.debug("load@GeoserverLoader - return checked deletion {}", this.getDeletion());
				return this.getDeletion();
			} else {
				JsonObject obj = getObject();
				if (obj == null) {
					this.checkDeletions();
					log.debug("load@GeoserverLoader - return not checked deletion {}", this.getDeletion());
					return this.getDeletion();
				}
				return obj;
			}

		}

	}

	private JsonObject getObject() {
		// Add one more to index as it starts at -1 and objects start at 0
		currentIndex++;
		JsonElement toReturn;
		try {
			// Try to recover object
			toReturn = loaded.get(currentIndex);
			log.debug("getObject@GeoserverLoader - get :: {}", toReturn.toString());
		} catch (IndexOutOfBoundsException e) {
			// End of objects if loop has no end we put loaded as null so next time (after
			// sleep) it
			// recover all objects again
			return null;
		}
		if (toReturn != null) {
			toReturn = transformForTransformer(toReturn.getAsJsonObject());
			log.debug("getObject@GeoserverLoader - getAsJsonObject :: {}", toReturn.toString());
			if (toReturn == null) {
				// already treated get next recursively
				toReturn = getObject();
			}
		}

		return toReturn != null ? toReturn.getAsJsonObject() : null;
	}

	private JsonObject transformForTransformer(JsonObject object) {

		Pair<String, String> ids = getIds(object);
		String localId = ids.getLeft();
		String globalId = ids.getRight();
		
		boolean alreadyTreated = false;
		for (int j = 0; j < this.treatedGUIDs.size() && !alreadyTreated; j++) {
			alreadyTreated = this.treatedGUIDs.get(j).equals(globalId);
		}

		if (!alreadyTreated) {
			this.treatedGUIDs.add(globalId);
			LoaderJsonObject loaderJson = new LoaderJsonObject();
			loaderJson.setGlobalId(globalId);
			loaderJson.setElement(object);
			log.debug("transformForTransformer@GeoserverLoader - globalId {} not treated add to treatedGUIDs", globalId);
			return gson.fromJson(gson.toJson(loaderJson), JsonObject.class);
		} else {
			return null;
		}

	}

	private JsonObject loadResponse(long timeout) {

		log.debug("loadResponse@GeoserverLoader - init with timeout '{}'",timeout);
		RequestConfig.Builder requestBuilder = RequestConfig.custom();
		requestBuilder.setConnectTimeout((int) timeout);
		requestBuilder.setConnectionRequestTimeout((int) timeout);
		HttpClientBuilder clientBuilder = HttpClients.custom();
		clientBuilder.setDefaultRequestConfig(requestBuilder.build());
		HttpClient httpClient = clientBuilder.build();
		String authType = this.params.getParamValue(GeoserverLoaderConfigKeys.LOADER_AUTH.getKey());
		String authHeader = "";
		if (authType != null && authType.equals("Basic")) {
			String auth = this.params.getParamValue(GeoserverLoaderConfigKeys.LOADER_USERNAME.getKey()) + ":"
					+ this.params.getParamValue(GeoserverLoaderConfigKeys.LOADER_PASSWORD.getKey());
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
			authHeader = "Basic " + new String(encodedAuth);
		}
		String uri = this.params.getParamValue(GeoserverLoaderConfigKeys.LOADER_URL.getKey());
		String params = this.params.getParamValue(GeoserverLoaderConfigKeys.LOADER_PARAMS.getKey());
		List<String> layers = this.params
				.getMultipleParamValues(GeoserverLoaderConfigKeys.LOADER_LAYERS_MULTIPLE.getKey());
		String layersInline = "";
		for (int i = 0; i < layers.size(); i++) {
			layersInline += (i != 0 ? "," + layers.get(i) : layers.get(i));
		}
		String format = this.params.getParamValue(GeoserverLoaderConfigKeys.LOADER_FORMAT.getKey());
		RequestBuilder request = RequestBuilder.get()
				.setUri(uri + params + "&typeName=" + layersInline + "&outputFormat=" + format);
		if (authType != null && authType.equals("Basic")) {
			request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		}

		JsonObject jsonResponse;

		try {
			log.debug("loadResponse@GeoserverLoader - execute httpClient built");
			Gson gson = new Gson();
			HttpResponse response = httpClient.execute(request.build());
			String bodyResp = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			jsonResponse = gson.fromJson(bodyResp, JsonObject.class);

		} catch (ConnectTimeoutException | SocketTimeoutException e) {
			log.error("loadResponse@GeoserverLoader - timeout while sending petition : ", e);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "timeout while sending petition to geoserver",
					e.getMessage());
		} catch (Exception e) {
			log.error("loadResponse@GeoserverLoader - exception while sending petition : ", e);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
					"exception while sending petition to geoserver", e.getMessage());
		}

		try {
			all = jsonResponse.get("features").getAsJsonArray();
			JsonArray arrayFiltered = new JsonArray();
			Date updateDate = null;
			String filterField = this.params.getParamValue(GlobalLoaderConfigKeys.LOADER_FILTER_FIELD.getKey(), false);

			if (this.page.getContent().isEmpty() || this.page.getContent().size() < 2 || filterField == null) {
				log.debug("loadResponse@GeoserverLoader - page is empty or filerField is null");

				for (JsonElement e : all) {
					Pair<String, String> ids = getIds(e);
					String localId = ids.getLeft();
					String globalId = ids.getRight();
					
					this.guidsExisting.put(localId, globalId);
					this.allGUIDs.add(globalId);
				}
				log.debug("loadResponse@GeoserverLoader - sort all of global id");
				QuickSortAlgorithm sorter = new QuickSortAlgorithm();
				String[] arr = this.allGUIDs.toArray(new String[this.allGUIDs.size()]);
				sorter.sort(arr);
		        List<String> list = new ArrayList<>();
		        Collections.addAll(list, arr);
		        this.allGUIDs = list;
				this.loaded = all;
				
			} else {
				updateDate = this.page.getContent().get(1).getExecutionStartDate();
				SimpleDateFormat sdf = new SimpleDateFormat(this.params.getParamValue(GlobalLoaderConfigKeys.LOADER_FILTER_FORMAT.getKey()));

				log.debug("loadResponse@GemwebLoader - filter all pair of guid to return");
				for (JsonElement e : all) {
					Pair<String, String> ids = getIds(e);
					String localId = ids.getLeft();
					String globalId = ids.getRight();
					
					this.guidsExisting.put(localId, globalId);
					this.allGUIDs.add(globalId);
					String dateUpdateElemStr = JsonPath.parse(e.toString()).<String>read(filterField);
					if (dateUpdateElemStr != null && !dateUpdateElemStr.isBlank()) {
						Date dateUpdateElem = sdf.parse(dateUpdateElemStr);
						if (dateUpdateElem.compareTo(updateDate) > 0) {
							arrayFiltered.add(e);
							log.debug("element added : {}", e.toString());
						}	
					} else {
						arrayFiltered.add(e);
						log.debug("element added : {}", e.toString());
					}

				}
				log.debug("loadResponse@GeoserverLoader - sort all of global id");
				QuickSortAlgorithm sorter = new QuickSortAlgorithm();
				String[] arr = this.allGUIDs.toArray(new String[this.allGUIDs.size()]);
				sorter.sort(arr);
		        List<String> list = new ArrayList<>();
		        Collections.addAll(list, arr);
		        this.allGUIDs = list;
				this.loaded = arrayFiltered;
			}
			return getObject();
		} catch (Exception e) {
			log.error("loadResponse@GeoserverLoader - exception while sending petition : ", e);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
					"exception while sending petition to geoserver", e.getMessage());

		}

	}
	
	private Pair<String, String> getIds(JsonElement e) {
		String localId = JsonPath.parse(e.toString())
				.<String>read(this.params.getParamValue(GeoserverLoaderConfigKeys.LOADER_JSON_PATH_LOCAL_ID.getKey()));
		String globalId = null;
		
		String hasGuid = this.guidsExisting.get(localId);
		log.debug("getIds@GeoserverLoader - has guid {}, local id :: {}, guids size {}", hasGuid != null, localId, this.guidsExisting.size());
		if (hasGuid == null) {
			globalId = invUtils.getGuid();
			GlobalIdDb guidDb = new GlobalIdDb();
			guidDb.setGlobalId(globalId);
			guidDb.setInventoryName(this.inventoryName);
			guidDb.setLocalId(localId);
			globalIdRepo.save(guidDb);
		} else {
			globalId = hasGuid;
		}
		return Pair.of(localId, globalId);
	}

	@Override
	public void stop() {
		this.instance.stop();
	}

}
