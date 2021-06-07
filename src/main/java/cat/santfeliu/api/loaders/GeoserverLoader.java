package cat.santfeliu.api.loaders;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;

import cat.santfeliu.api.beans.LoaderJsonObject;
import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.enumerator.GeoserverLoaderConfigKeys;
import cat.santfeliu.api.exceptions.ApiErrorException;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.utils.InventoryUtils;

public class GeoserverLoader extends ConnectorLoader {

	private static final Logger log = LoggerFactory.getLogger(GeoserverLoader.class);
	
	private int currentIndex = -1;
	
	private JsonArray loaded = null;

	@Override
	public JsonObject load(int timeout) {
		if (loaded == null) {
			return loadResponse(timeout);
		} else {
			if (this.checkedDeletions) {
				return this.getDeletion();
			} else {
				JsonObject obj = getObject();
				if (obj == null) {
					this.checkDeletions();
					return this.getDeletion();
				}
				return obj;
			}
	
		}
		
	}
	
	private JsonObject getObject() {		
		currentIndex++;
		JsonElement toReturn;
		try { 
			toReturn = loaded.get(currentIndex);
		} catch (IndexOutOfBoundsException e) {
			toReturn = null;
		}
		if (toReturn != null) {
			toReturn = transformForTransformer(toReturn.getAsJsonObject());
			if (toReturn == null) {
				// already treated get next recursively
				toReturn = getObject();
			}
		}
		
		return toReturn != null ? toReturn.getAsJsonObject() : null;
	}
	
	private JsonObject transformForTransformer(JsonObject object) {
		String localId = JsonPath.parse(object.toString()).<String>read(
				this.params.getParamValue(GeoserverLoaderConfigKeys.LOADER_JSON_PATH_LOCAL_ID.getKey()));
		String globalId = null;
		Optional<GlobalIdDb> hasIdOpt = globalIdRepo.findByInventoryAndLocalId(this.inventoryName, localId);
		if (hasIdOpt.isEmpty()) {
			// No s'ha trobat GUID cal obtenir-lo i inserir-lo
			globalId = InventoryUtils.getGuid();
			GlobalIdDb guid = new GlobalIdDb();
			guid.setGlobalId(globalId);
			guid.setInventoryName(this.inventoryName);
			guid.setLocalId(localId);
			globalIdRepo.save(guid);
		} else {
			globalId = hasIdOpt.get().getGlobalId();
		}
		boolean alreadyTreated = false;
		for (int j = 0; j < this.treatedGUIDs.size() && !alreadyTreated; j++) {
			alreadyTreated = this.treatedGUIDs.get(j).equals(globalId);
		}

		if (!alreadyTreated) {
			this.treatedGUIDs.add(globalId);
			LoaderJsonObject loaderJson = new LoaderJsonObject();
			loaderJson.setGlobalId(globalId);
			loaderJson.setElement(object);
			return gson.fromJson(gson.toJson(loaderJson), JsonObject.class);
		} else {
			return null;
		}

	}
	
	private JsonObject loadResponse(int timeout) {
		RequestConfig.Builder requestBuilder = RequestConfig.custom();
		requestBuilder.setConnectTimeout(timeout);
		requestBuilder.setConnectionRequestTimeout(timeout);
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
		List<String> layers = this.params.getMultipleParamValues(GeoserverLoaderConfigKeys.LOADER_LAYERS_MULTIPLE.getKey());
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
			Gson gson = new Gson();
			HttpResponse response = httpClient.execute(request.build());
			String bodyResp = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			jsonResponse = gson.fromJson(bodyResp, JsonObject.class);
			
		} catch (ConnectTimeoutException | SocketTimeoutException e) {
			log.error("loadResponse@GeoserverLoader - timeout while sending petition : ", e);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
					"timeout while sending petition to geoserver", e.getMessage());
		} catch (Exception e) {
			log.error("loadResponse@GeoserverLoader - exception while sending petition : ", e);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
					"exception while sending petition to geoserver", e.getMessage());
		}

		try {
			this.loaded = jsonResponse.get("features").getAsJsonArray();
			return getObject();
		} catch (Exception e) {
			log.error("loadResponse@GeoserverLoader - exception while sending petition : ", e);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
					"exception while sending petition to geoserver", e.getMessage());
		
		}

	}

	@Override
	public void stop() {
		this.instance.stop();
	}

}
