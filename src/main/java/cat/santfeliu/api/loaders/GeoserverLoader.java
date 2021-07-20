package cat.santfeliu.api.loaders;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cat.santfeliu.api.utils.ConfigProperty;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.JsonElement;
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

	// PARAMS
	@ConfigProperty(name="url", description="Geoserver wfs url")
	String url;

	@ConfigProperty(name="params", description="Geoserver url query params")
	String urlParams;

	@ConfigProperty(name="username", description="User used for basic authentication")
	String username;

	@ConfigProperty(name="password", description="Password used for basic authentication", hidden=true)
	String password;

	@ConfigProperty(name="layers.*", description="Layers to load from geoserver can be multiple like this: layers.01, layers.02...")
	List<String> layersLoad;

	@ConfigProperty(name="format", description="Format of petition's body sent to geoserver")
	String formatPetition;

	@ConfigProperty(name="auth", description="Type of authentication currently only Basic is supported")
	String auth;

	@ConfigProperty(name="json.path.local.id", description="Json path route in element leading to local id")
	String jsonPathLocalId;

	@ConfigProperty(name="json.path.array.element", description="Path route in geoserver response leading to array with elements")
	String jsonPathArrayElement;

	@ConfigProperty(name = "filter.field", description = "Param that indicates with which field the date of update of the object corresponds, if it is not indicated a full load is made")
	String filter_Field;

	@ConfigProperty(name = "filter.format", description = "Indicates update date format")
	String filterFormat;

	@Override
	public JsonNode load(long timeout) {
		if (loaded == null && this.reset) {
			this.checkedDeletions = false;
			this.reset = false;
			log.debug("load@GeoserverLoader - load response of timeout {}", timeout);
			return loadResponse(timeout);
		} else {
			if (this.checkedDeletions) {
				log.debug("load@GeoserverLoader - return checked deletion {}", this.getDeletion());
				return this.getDeletion();
			} else {
				JsonNode obj = getObject();
				if (obj == null) {
					this.checkDeletions();
					log.debug("load@GeoserverLoader - return not checked deletion {}", this.getDeletion());
					return this.getDeletion();
				}
				return obj;
			}

		}

	}

	private JsonNode getObject() {
		// Add one more to index as it starts at -1 and objects start at 0
		currentIndex++;
		JsonNode toReturn = null;
		try {
			// Try to recover object
			toReturn = loaded.get(currentIndex);
			log.debug("getObject@GeoserverLoader - get :: {}", mapper.writeValueAsString(toReturn));
		} catch (IndexOutOfBoundsException e) {
			// End of objects if loop has no end we put loaded as null so next time (after
			// sleep) it
			// recover all objects again
			return null;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if (toReturn !=  null) {
			toReturn = transformForTransformer(toReturn);
			try {
				log.debug("getObject@GeoserverLoader - getAsJsonObject :: {}", mapper.writeValueAsString(toReturn));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			if (toReturn == null) {
				// already treated get next recursively
				toReturn = getObject();
			}
		}

		return toReturn != null ? toReturn : null;
	}

	private JsonNode transformForTransformer(JsonNode node) {

		Pair<String, String> ids = getIds(node);
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
			loaderJson.setElement(node);
			log.debug("transformForTransformer@GeoserverLoader - globalId {} not treated add to treatedGUIDs",
					globalId);
			try {
				return mapper.readTree(mapper.writeValueAsString(loaderJson));
			} catch (JsonProcessingException e) {
				// bever happens
				return null;
			}
		} else {
			return null;
		}

	}

	private JsonNode loadResponse(long timeout) {

		log.debug("loadResponse@GeoserverLoader - init with timeout '{}'", timeout);
		RequestConfig.Builder requestBuilder = RequestConfig.custom();
		requestBuilder.setConnectTimeout((int) timeout);
		requestBuilder.setConnectionRequestTimeout((int) timeout);
		HttpClientBuilder clientBuilder = HttpClients.custom();
		clientBuilder.setDefaultRequestConfig(requestBuilder.build());
		HttpClient httpClient = clientBuilder.build();
		String authType = auth;
		String authHeader = "";
		if (authType != null && authType.equals("Basic")) {
			String auth = username + ":" + password;
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
			authHeader = "Basic " + new String(encodedAuth);
		}
		String uri = url;
		String params = urlParams;
		List<String> layers = layersLoad;
		String layersInline = "";
		for (int i = 0; i < layers.size(); i++) {
			layersInline += (i != 0 ? "," + layers.get(i) : layers.get(i));
		}
		String format = formatPetition;
		RequestBuilder request = RequestBuilder.get()
				.setUri(uri + params + "&typeName=" + layersInline + "&outputFormat=" + format);
		if (authType != null && authType.equals("Basic")) {
			request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		}

		JsonNode jsonResponse;

		try {
			log.debug("loadResponse@GeoserverLoader - execute httpClient built");
			HttpResponse response = httpClient.execute(request.build());
			String bodyResp = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			jsonResponse = mapper.readTree(bodyResp);

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
			all = mapper.valueToTree(jsonResponse.get("features"));
			ArrayNode arrayFiltered = mapper.createArrayNode();
			Date updateDate = null;
			String filterField = filter_Field;

			if (this.page.getContent().isEmpty() || this.page.getContent().size() < 2 || filterField == null) {
				log.debug("loadResponse@GeoserverLoader - page is empty or filerField is null");
				if (all.isArray()) {
					for (JsonNode e : all) {
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
				}

			} else {
				updateDate = this.page.getContent().get(1).getExecutionStartDate();
				SimpleDateFormat sdf = new SimpleDateFormat(filterFormat);
				log.debug("loadResponse@GemwebLoader - filter all pair of guid to return");
				if (all.isArray()) {
					for (JsonNode e : all) {
						Pair<String, String> ids = getIds(e);
						String localId = ids.getLeft();
						String globalId = ids.getRight();

						this.guidsExisting.put(localId, globalId);
						this.allGUIDs.add(globalId);
						String dateUpdateElemStr = JsonPath.parse(mapper.writeValueAsString(e))
								.<String>read(filterField);
						if (dateUpdateElemStr != null && !dateUpdateElemStr.isBlank()) {
							Date dateUpdateElem = sdf.parse(dateUpdateElemStr);
							if (dateUpdateElem.compareTo(updateDate) > 0) {
								arrayFiltered.add(e);
								log.debug("element added : {}", mapper.writeValueAsString(e));
							}
						} else {
							arrayFiltered.add(e);
							log.debug("element added : {}", mapper.writeValueAsString(e));
						}

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

	private Pair<String, String> getIds(JsonNode e) {
		String localId = null;
		try {
			localId = JsonPath.parse(mapper.writeValueAsString(e)).<String>read(jsonPathLocalId);
		} catch (JsonProcessingException jsonProcessingException) {
			jsonProcessingException.printStackTrace();
		}
		String globalId = null;

		String hasGuid = this.guidsExisting.get(localId);
		log.debug("getIds@GeoserverLoader - has guid {}, local id :: {}, guids size {}", hasGuid != null, localId,
				this.guidsExisting.size());
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
