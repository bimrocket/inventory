package cat.santfeliu.api.loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;

import cat.santfeliu.api.beans.LoaderJsonObject;
import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.enumerator.GemWebLoaderConfigKeys;
import cat.santfeliu.api.enumerator.GeoserverLoaderConfigKeys;
import cat.santfeliu.api.enumerator.GlobalLoaderConfigKeys;
import cat.santfeliu.api.exceptions.ApiErrorException;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.utils.QuickSortAlgorithm;
import net.minidev.json.JSONArray;

public class GemwebLoader extends ConnectorLoader {

	private static final Logger log = LoggerFactory.getLogger(GeoserverLoader.class);

	@Override
	public JsonObject load(long timeout) {
		if (loaded == null && this.reset) {
			this.checkedDeletions = false;
			this.reset = false;
			try {
				return loadResponse(this.getAccessToken());
			} catch (Exception e) {
				return null;
			}
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
		// Add one more to index as it starts at -1 and objects start at 0
		currentIndex++;
		JsonElement toReturn;
		try {
			// Try to recover object
			toReturn = loaded.get(currentIndex);
		} catch (IndexOutOfBoundsException e) {
			// End of objects if loop has no end we put loaded as null so next time (after
			// sleep) it
			// recover all objects again
			return null;
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
			return gson.fromJson(gson.toJson(loaderJson), JsonObject.class);
		} else {
			return null;
		}

	}

	/**
	 * Envia un request de tipus POST.
	 * 
	 * @param uri Servidor de tercers
	 * @param xml XML a enviar
	 * @return XML de retorn
	 * @throws Exception
	 */
	public JsonObject loadResponse(String token) throws Exception {

		Instant start = Instant.now();

		String uri = this.params.getParamValue(GemWebLoaderConfigKeys.LOADER_URL.getKey());

		try {
			int connectTimeout = (int) this.loadTimeout;
			int socketTimeout = (int) this.loadTimeout;

			log.debug("loadResponse@GemwebLoader - init with uri '{}' and token '{}'", uri, token);
			HttpPost httpPost = new HttpPost(uri);
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
					.setSocketTimeout(socketTimeout).build();
			String authType = this.params.getParamValue(GemWebLoaderConfigKeys.LOADER_AUTH.getKey());
			String authHeader = "";
			if (authType != null && authType.equals("Bearer")) {
				authHeader = "Bearer " + new String(token);
				httpPost.setHeader("Authorization", authHeader);
			}
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("request", "get_inventory"));
			nvps.add(new BasicNameValuePair("access_token", token));
			nvps.add(new BasicNameValuePair("category",
					this.params.getParamValue(GemWebLoaderConfigKeys.LOADER_CATEGORY.getKey())));

			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			httpPost.setConfig(requestConfig);
			httpPost.setHeader("Accept", "application/xml");
			httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
			HttpResponse resp = HttpClientBuilder.create().build().execute(httpPost);

			String ret = inputStreamResponseToString(resp.getEntity().getContent());
			String json = XML.toJSONObject(ret).toString();
			/*
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			Document doc = null;
			all = new JsonArray();
			XmlMapper xmlMapper = new XmlMapper();

			try (InputStream targetStream = new ByteArrayInputStream(ret.getBytes());) {

				// parse XML file
				DocumentBuilder db = dbf.newDocumentBuilder();

				doc = db.parse(targetStream);

				System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
				System.out.println("------");
				xmlMapper.readTree(new Xembler(
						   new Directives()
						     .xpath(this.params.getParamValue(GemWebLoaderConfigKeys.LOADER_XPATH_ELEMENT_ARRAY.getKey()))
						 ).apply(doc).g;
				if (doc.getChildNodes().getLength() != 0) {
					log.info(doc.getChildNodes().toString());
				}

			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
			if (log.isDebugEnabled()) {
				log.debug("loadResponse@GemwebLoader - response '{}'", ret);
			}
			*/
//			
//			if (node.get("error") != null) {
//				log.error("loadResponse@GemwebLoader - error in sending pettion to gemweb, error responded : {}", node.get("error").asText());
//				return null;
//			}

			try {
				JSONArray array  = JsonPath.parse(json).<JSONArray>read(this.params.getParamValue(GemWebLoaderConfigKeys.LOADER_JSON_PATH_ELEMENT_ARRAY.getKey())); 
				all = gson.fromJson(array.toJSONString(), JsonArray.class);
				JsonArray arrayFiltered = new JsonArray();
				Date updateDate = null;
				String filterField = this.params.getParamValue(GlobalLoaderConfigKeys.LOADER_FILTER_FIELD.getKey(),
						false);
				if (this.page.getContent().isEmpty() || this.page.getContent().size() < 2 || filterField == null) {
					this.loaded = all;
				} else {
					updateDate = this.page.getContent().get(1).getExecutionStartDate();
					SimpleDateFormat sdf = new SimpleDateFormat(
							this.params.getParamValue(GlobalLoaderConfigKeys.LOADER_FILTER_FORMAT.getKey()));

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
				log.error("loadResponse@GemwebLoader - exception while sending petition : ", e);
				throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
						"exception while sending petition to gemweb", e.getMessage());

			}

		} catch (SocketTimeoutException e) {
			log.error("loadResponse@GemwebLoader - Error sending POST to '{}' readTimeout '{}' with xml", uri, 60000);
			throw e;
		} catch (UnsupportedEncodingException e) {
			log.error("loadResponse@GemwebLoader - Error sending POST to '{}'.", uri, e);
			throw e;
		} catch (ClientProtocolException e) {
			log.error("loadResponse@GemwebLoader - Error sending POST to '{}''.", uri, e);
			throw e;
		} catch (IOException e) {
			log.error("loadResponse@GemwebLoader - Error sending POST to '{}'.", uri, e);
			throw e;
		} catch (Exception e) {
			log.error("loadResponse@GemwebLoader - Error sending POST to '{}'.", uri, e);
			throw e;
		}
	}

	/**
	 * Envia un request de tipus POST.
	 * 
	 * @param uri Servidor de tercers
	 * @param xml XML a enviar
	 * @return XML de retorn
	 * @throws Exception
	 */
	public String getAccessToken() throws Exception {

		Instant start = Instant.now();

		String uri = this.params.getParamValue(GemWebLoaderConfigKeys.LOADER_URL.getKey());

		try {
			int connectTimeout = (int) this.loadTimeout;
			int socketTimeout = (int) this.loadTimeout;

			log.debug("getAccessToken@GemwebLoader - init with uri '{}'", uri);
			HttpPost httpPost = new HttpPost(uri);
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
					.setSocketTimeout(socketTimeout).build();
			/*
			 * String authType =
			 * this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_AUTH.getKey());
			 * String authHeader = ""; if (authType != null && authType.equals("Basic")) {
			 * String auth =
			 * this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_USERNAME.getKey
			 * ()) + ":" +
			 * this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_PASSWORD.getKey
			 * ()); byte[] encodedAuth =
			 * Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1)); authHeader =
			 * "Basic " + new String(encodedAuth); }
			 */
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("request", "get_token"));
			nvps.add(new BasicNameValuePair("client_id",
					this.params.getParamValue(GemWebLoaderConfigKeys.LOADER_CLIENT_ID.getKey())));
			nvps.add(new BasicNameValuePair("client_secret",
					this.params.getParamValue(GemWebLoaderConfigKeys.LOADER_CLIENT_SECRET.getKey())));
			nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));

			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			httpPost.setConfig(requestConfig);
			httpPost.setHeader("Accept", "application/xml");
			httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
			HttpResponse resp = HttpClientBuilder.create().build().execute(httpPost);

			String ret = inputStreamResponseToString(resp.getEntity().getContent());

			XmlMapper xmlMapper = new XmlMapper();
			JsonNode node = xmlMapper.readTree(ret.getBytes(StandardCharsets.UTF_8));
			String accessToken = "";

			if (log.isDebugEnabled()) {
				log.debug("getAccessToken@GemwebLoader - response '{}'", ret);
			}

			if (node.get("error") != null) {
				log.error("getAccessToken@GemwebLoader - error in authentication, error responded : {}",
						node.get("resultat").get("error"));
			}
			JsonNode resultat = node.get("access_token");
			if (resultat != null) {
				accessToken = resultat.asText();
			}

			return accessToken;

		} catch (SocketTimeoutException e) {
			log.error("getAccessToken@GemwebLoader - Error sending POST to '{}' readTimeout '{}' with xml", uri, 60000);
			throw e;
		} catch (UnsupportedEncodingException e) {
			log.error("getAccessToken@GemwebLoader - Error sending POST to '{}'.", uri, e);
			throw e;
		} catch (ClientProtocolException e) {
			log.error("getAccessToken@GemwebLoader - Error sending POST to '{}''.", uri, e);
			throw e;
		} catch (IOException e) {
			log.error("getAccessToken@GemwebLoader - Error sending POST to '{}'.", uri, e);
			throw e;
		} catch (Exception e) {
			log.error("getAccessToken@GemwebLoader - Error sending POST to '{}'.", uri, e);
			throw e;
		}
	}

	private String inputStreamResponseToString(InputStream reader) {
		StringBuilder sb = new StringBuilder();

		try (InputStreamReader inputStreamReader = new InputStreamReader(reader)) {
			BufferedReader br = new BufferedReader(inputStreamReader);
			String readLine;
			while (((readLine = br.readLine()) != null)) {
				sb.append("\n").append(readLine);
			}
		} catch (IOException e) {
			log.error("inputStreamResponseToString@GemwebLoader - Error reading response", e);
		}

		String ret = sb.toString().replaceAll("\r\n", "").replaceAll("\t", "").replaceAll("\n", "");
		return ret;
	}

	private Pair<String, String> getIds(JsonElement e) {
		String localId = JsonPath.parse(e.toString())
				.<String>read(this.params.getParamValue(GeoserverLoaderConfigKeys.LOADER_JSON_PATH_LOCAL_ID.getKey()));
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
