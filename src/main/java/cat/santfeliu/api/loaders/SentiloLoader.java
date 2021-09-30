package cat.santfeliu.api.loaders;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
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
import com.jayway.jsonpath.JsonPath;

import cat.santfeliu.api.beans.LoaderJsonObject;
import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.exceptions.ApiErrorException;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.utils.ConfigProperty;

public class SentiloLoader extends ConnectorLoader {

	private static final Logger log = LoggerFactory.getLogger(SentiloLoader.class);

	// PARAMS
	@ConfigProperty(name = "url", description = "Sentilo API URL")
	String url;

	@ConfigProperty(name = "identity.key", description = "Authorization key to access api (in http headers this equals IDENTITY_KEY)")
	String identityKey;

	@ConfigProperty(name = "omit.providers.*", description = "Which providers catalog you don't want to load, can be muliple: omit.providers.01, omit.providers.02...")
	List<String> omitProviders;

	@ConfigProperty(name = "json.path.local.id", description = "Json path route in element leading to local id")
	String jsonPathLocalId;

	@ConfigProperty(name = "filter.field", description = "Param that indicates with which field the date of update of the object corresponds, if it is not indicated a full load is made", required = false)
	String filterField;

	@ConfigProperty(name = "filter.format", description = "Indicates update date format", required = false)
	String filterFormat;

	@Override
	public JsonNode load(long timeout) {

		if (loaded == null && this.reset) {
			this.checkedDeletions = false;
			this.reset = false;
			log.debug("load@SentiloLoader - {} - load response of timeout {}", this.connectorName, timeout);
			return loadResponse(timeout);
		} else {
			if (this.checkedDeletions) {
				log.debug("load@SentiloLoader - {} - return checked deletion", this.connectorName);
				return this.getDeletion();
			} else {
				JsonNode obj = getObject();
				if (obj == null) {
					this.checkDeletions();
					log.debug("load@SentiloLoader - {} - return not checked deletion", this.connectorName);
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
			log.debug("getObject@SentiloLoader - {} - get :: {}", this.connectorName,
					mapper.writeValueAsString(toReturn));
		} catch (IndexOutOfBoundsException e) {
			// End of objects if loop has no end we put loaded as null so next time (after
			// sleep) it
			// recover all objects again
			return null;
		} catch (JsonProcessingException e) {
			this.senError("ERROR_LOAD_JSON_PROCESSING").describe("Generic json exception").exception(e).foundErr();
			log.error("getObject@SentiloLoader - {} - exception", this.connectorName, e);
		}
		if (toReturn != null) {
			toReturn = transformForTransformer(toReturn);

			if (toReturn == null) {
				// already treated get next recursively
				toReturn = getObject();
			} else {
				log.debug("getObject@SentiloLoader - {} - getAsJsonObject :: {}", this.connectorName, toReturn);

			}
		}

		return toReturn != null ? toReturn : null;
	}

	private JsonNode transformForTransformer(JsonNode node) {

		Pair<String, String> ids = getIds(node);
		String globalId = ids.getRight();

		var alreadyTreated = false;
		for (int j = 0; j < this.treatedGUIDs.size() && !alreadyTreated; j++) {
			alreadyTreated = this.treatedGUIDs.get(j).equals(globalId);
		}

		if (!alreadyTreated) {
			this.treatedGUIDs.add(globalId);
			var loaderJson = new LoaderJsonObject();
			loaderJson.setGlobalId(globalId);
			loaderJson.setElement(node);
			log.debug("transformForTransformer@SentiloLoader - {} - globalId {} not treated add to treatedGUIDs",
					this.connectorName, globalId);
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

		log.debug("loadResponse@SentiloLoader - {} - init with timeout '{}' and uri '{}'", this.connectorName, timeout,
				url);
		RequestConfig.Builder requestBuilder = RequestConfig.custom();
		requestBuilder.setConnectTimeout((int) timeout);
		requestBuilder.setConnectionRequestTimeout((int) timeout);
		HttpClientBuilder clientBuilder = HttpClients.custom();
		clientBuilder.setDefaultRequestConfig(requestBuilder.build());
		HttpClient httpClient = clientBuilder.build();
		String authHeader = "IDENTITY_KEY";
		String uri = url;
		String authKey = identityKey;
		List<JsonNode> responseForEachProvider = new ArrayList<JsonNode>();

		RequestBuilder request = RequestBuilder.get().setUri(uri + "/catalog/");
		request.setHeader(authHeader, authKey);
		request.setHeader("Accept", "application/json");
		JsonNode jsonResponse;

		try {
			log.debug("loadResponse@SentiloLoader - {} - execute with HttpCLient the HttpRequest made",
					this.connectorName);
			HttpResponse response = httpClient.execute(request.build());
			var bodyResp = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			jsonResponse = mapper.readTree(bodyResp);

		} catch (ConnectTimeoutException e) {
			log.error("loadResponse@SentiloLoader - {} - timeout while sending petition : ", this.connectorName, e);
			this.senError("LOADER_CONNECT_LOAD_TIMEOUT").describe("There was connect timeout while contacting sentilo")
					.foundErr().exception(e);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "timeout while sending petition to sentilo",
					e.getMessage());

		} catch (SocketTimeoutException e) {
			log.error("loadResponse@SentiloLoader - {} - timeout while sending petition : ", this.connectorName, e);
			this.senError("LOADER_SOCKET_LOAD_TIMEOUT").describe("There was socket timeout while contacting sentilo")
					.foundErr().exception(e);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "timeout while sending petition to sentilo",
					e.getMessage());

		} catch (Exception e) {
			log.error("loadResponse@SentiloLoader - {} - exception while sending petition : ", this.connectorName, e);
			this.senError("LOADER_GENERIC_EXCEPTION").describe("There was exception while contacting sentilo")
					.foundErr().exception(e);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "exception while sending petition to sentilo",
					e.getMessage());
		}

		if (jsonResponse == null || jsonResponse.isNull() || jsonResponse.get("providers") == null || jsonResponse.get("providers").isNull())  {
			var error = "There was error while reading sentilo respònse, it's empty";
			log.error(
					"loadResponse@SentiloLoader - {} - error while reading responses from sentilo, no response found ",
					this.connectorName);
			this.senError("LOADER_EMPTY_RESPONSE").describe(error).foundErr();
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, error, error);
		}

		try {
			all = mapper.createArrayNode();
			ArrayNode providersArray =  mapper.valueToTree(jsonResponse.get("providers"));
			providersArray.forEach((providerJson) ->  {
				JsonNode nodeProvider = providerJson.get("provider");
				String provider = (nodeProvider != null && !nodeProvider.isNull()) ? nodeProvider.asText() : "";
				if (provider != "") {
					boolean allowed = true;
					for (int i = 0; i < omitProviders.size(); i++) {
						if (provider.equals(omitProviders.get(i))) {
							allowed = false;
						}
					}
					if (allowed) {
						try { 
							ArrayNode sensorsArray = mapper.valueToTree(providerJson.get("sensors"));
							all.addAll(sensorsArray);
						} catch (Exception e) {
							var error = "There was error while reading sentilo respònse, provider hasn't got defined sensors array or it's not a array , provider : " + providerJson.get("provider").asText();
							log.error(
									"loadResponse@SentiloLoader - {} - error while reading responses from sentilo, " + "provider hasn't got defined sensors array or it's not a array , provider : " + providerJson.get("provider").asText(),
									this.connectorName);
							this.senError("LOADER_PROVIDER_HAS_NO_SENSORS").describe(error).foundErr();
							throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, error, error);	
						}
					}
				}
				
			});
			
			ArrayNode arrayFiltered = mapper.createArrayNode();
			Date updateDate = null;

			if (this.page.getContent().isEmpty() || this.page.getContent().size() < 2 || filterField == null) {
				log.debug("loadResponse@SentiloLoader - {} - page is empty or filerField is null",
						this.connectorName);
				if (all.isArray()) {
					for (JsonNode e : all) {
						Pair<String, String> ids = getIds(e);
						String localId = ids.getLeft();
						String globalId = ids.getRight();

						this.guidsExisting.put(localId, globalId);
						this.allGUIDs.add(globalId);
					}
					log.debug("loadResponse@SentiloLoader - {} - sort all of global id", this.connectorName);
					String[] arr = this.allGUIDs.toArray(new String[this.allGUIDs.size()]);
					Arrays.sort(arr);
					List<String> list = new ArrayList<>();
					Collections.addAll(list, arr);
					this.allGUIDs = list;
					this.loaded = all;
				}

			} else {
				updateDate = this.page.getContent().get(1).getExecutionStartDate();
				SimpleDateFormat sdf = new SimpleDateFormat(filterFormat);
				log.debug("loadResponse@SentiloLoader - {} - filter all pair of guid to return", this.connectorName);
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
								log.debug("loadResponse@SentiloLoader - {} - element added : {}", this.connectorName,
										mapper.writeValueAsString(e));
							}
						} else {
							arrayFiltered.add(e);
							log.debug("loadResponse@SentiloLoader - {} - element added : {}", this.connectorName,
									mapper.writeValueAsString(e));
						}

					}
				}
				log.debug("loadResponse@SentiloLoader - {} - sort all of global id", this.connectorName);
				String[] arr = this.allGUIDs.toArray(new String[this.allGUIDs.size()]);
				Arrays.sort(arr);
				List<String> list = new ArrayList<>();
				Collections.addAll(list, arr);
				this.allGUIDs = list;
				this.loaded = arrayFiltered;
			}
			return getObject();
		} catch (Exception e) {
			log.error("loadResponse@SentiloLoader - {} - exception while sending petition : ", this.connectorName, e);
			this.senError("LOADER_LOAD_EXCEPTION")
					.describe("Loader has found a fatal exception while retriving objects").foundErr().exception(e);
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
		log.debug("getIds@SentiloLoader - {} -  has guid {}, local id :: {}, guids size {}", this.connectorName,
				hasGuid != null, localId, this.guidsExisting.size());
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
