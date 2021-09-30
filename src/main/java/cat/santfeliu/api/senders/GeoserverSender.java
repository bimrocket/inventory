package cat.santfeliu.api.senders;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xembly.Directives;
import org.xembly.Xembler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cat.santfeliu.api.components.ConnectorSender;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.utils.ConfigProperty;
import cat.santfeliu.api.utils.GeometryXMLUtils;

public class GeoserverSender extends ConnectorSender {

	private static final Logger log = LoggerFactory.getLogger(GeoserverSender.class);
	ObjectMapper mapper = new ObjectMapper();

	// PARAMS
	@ConfigProperty(name="url", description="Url fins wfs de servidor geoserver")
	String url;

	@ConfigProperty(name = "layer", description = "Layer to update")
	String layer;

	@ConfigProperty(name = "type.name", description = "Nom de entitat a actualitza")
	String typeName;

	@ConfigProperty(name = "auth", description = "Authentication used currently supported only Basic")
	String auth;

	@ConfigProperty(name = "username", description = "User used for basic authentication")
	String username;

	@ConfigProperty(name = "password", description = "Password used for basic authentication", hidden=true)
	String password;
	
	@ConfigProperty(name = "petition.timeout" , description = "Timeout used to sending to geoserver (if it exceeds timeout then it retries by number of retries set")
	Integer timeout;
	
	@ConfigProperty(name = "petition.retries" , description = "Number of total retries when timeout occurs")
	Integer retries;

	@Override
	public void send(JsonNode node) {
		boolean insert = false;
		boolean update = false;
		boolean delete = false;
		String localId = "";
		String globalId = node.get("globalId").asText();
		log.debug("send@GeoserverSender - find globalidDb by inventory {} and globalid {}", this.inventoryName,
				globalId);
		Optional<GlobalIdDb> globalIdDbOpt = globalIdRepo.findByInventoryAndGlobalId(this.inventoryName, globalId);
		if (globalIdDbOpt.isPresent()) {
			if (node.get("element") == null) {
				delete = true;
			} else {
				update = true;
			}
			localId = globalIdDbOpt.get().getLocalId();
		} else if (node.get("element") != null) {
			insert = true;
		}
		Directives dir = new Directives();
		dir.add("Transaction").attr("xmlns", "http://www.opengis.net/wfs").attr("service", "WFS")
				.attr("version", "1.0.0").attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
				.attr("xsi:schemaLocation", "http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.1.0/wfs.xsd");
		if (insert) {
			JsonNode elem = node.get("element");
			log.debug("send@GeoserverSender - add insert of geoserversender key {}", layer);
			dir.add("Insert").add(layer);
			dir.attr("xmlns", "https://www.santfeliu.cat");
			Iterator<String> keys = elem.fieldNames();
			while (keys.hasNext()) {
				String key = keys.next();
				
				if (key.equals("geom") || key.startsWith("geom")) {
					JsonNode elemElem = null;
					try {
						JsonNode nodeGeom = elem.get(key);
						if (nodeGeom.isObject()) {
							elemElem = nodeGeom;
						} else if (nodeGeom.isValueNode()) {
							elemElem = (JsonNode) mapper.readValue((nodeGeom.asText()), ObjectNode.class);
						}
						
					} catch (JsonProcessingException e) {
						this.senError("SENDER_ERROR_MAPPING_GEOM").describe("can't map geom object as jsonObject").foundErr().exception(e);
						log.error("send@GeoserverSender - {} - can't map geom object as jsonObject {}", this.connectorName, elem.get(key).asText());
					}
					log.debug("send@GeoserverSender - {} - add geom key", this.connectorName); 
					dir.add(key);
					JsonNode geomObj = elemElem;
					if (geomObj != null && !geomObj.isNull()) {
						GeometryXMLUtils.putGeometryObjInXML(dir, geomObj);
					}
					dir.up();
				} else {
					JsonNode elemElem = null;
					elemElem = elem.get(key);
					dir.add(key);
					log.debug("send@GeoserverSender - {} - add not geom key {}", this.connectorName, key);
					if (elemElem == null || elemElem.isNull()) {
						dir.set("");
					} else {
						dir.set(elemElem.asText());
					}
					dir.up();
				}
			}

		} else if (update) {
			JsonNode elem = node.get("element");
			log.debug("send@GeoserverSender - {} - add update of geoserversender key {}", this.connectorName, typeName);
			dir.add("Update").attr("typeName", typeName);
			dir.attr("xmlns:sf", "https://www.santfeliu.cat");
			Iterator<String> keys = elem.fieldNames();
			while (keys.hasNext()) {
				String key = keys.next();
				dir.add("Property");
				if (key.equals("geom")) {
					JsonNode elemElem = null;
					try {
						JsonNode nodeGeom = elem.get(key);
						if (nodeGeom.isObject()) {
							elemElem = nodeGeom;
						} else if (nodeGeom.isValueNode()) {
							elemElem = (JsonNode) mapper.readValue((nodeGeom.asText()), ObjectNode.class);
						}
					} catch (JsonProcessingException e) {
						this.senError("SENDER_ERROR_MAPPING_GEOM").describe("can't map geom object as jsonObject").foundErr().exception(e);
						log.error("send@GeoserverSender - {} - can't map geom object as jsonObject {}", this.connectorName, elem.get(key).asText());
					}
					log.debug("send@GeoserverSender - {} - set geom key", this.connectorName);
					dir.add("Name").set("geom").up();
					JsonNode geomObj = elemElem;
					dir.add("Value");

					if (geomObj != null && !geomObj.isNull()) {
						GeometryXMLUtils.putGeometryObjInXML(dir, geomObj);
					}

					
					dir.up();
				} else {
					JsonNode elemElem = null;
					elemElem = elem.get(key);

					log.debug("send@GeoserverSender - {} - set another key {}", this.connectorName, key);
					dir.add("Name");
					dir.set(key);
					dir.up().add("Value");
					if (elemElem == null || elemElem.isNull()) {
						dir.set("");
					} else {
						dir.set(elemElem.asText());
					}
					dir.up();
				}
				dir.up();
			}
			dir.add("Filter").attr("xmlns", "http://www.opengis.net/ogc");
			dir.add("FeatureId").attr("fid", localId);
		} else {
			log.debug("send@GeoserverSender - {} - add delete of geoserversender key {}", this.connectorName ,typeName);
			dir.add("Delete").attr("typeName", typeName);
			dir.attr("xmlns:sf", "https://www.santfeliu.cat");
			dir.add("Filter").attr("xmlns", "http://www.opengis.net/ogc");
			dir.add("FeatureId").attr("fid", localId);
		}
		try {
			String xml = new Xembler(dir).xml().replaceAll("\r\n", "");
			log.debug("send@GeoserverSender - {} - xml done :: {}", this.connectorName, xml);
			String uri = url;
			String bodyResp = sendPostXML(uri, xml, retries);

			if (insert) {
				// an instance of builder to parse the specified xml file
				try {
					Matcher m = Pattern.compile("fid=\"[A-z_.0-9]+\"").matcher(bodyResp);
					while (m.find()) {
						localId = m.group(0).replaceAll("fid=", "").replaceAll("\"", "");
					}
					if (localId != null) {
						GlobalIdDb globalIdDb = new GlobalIdDb();
						globalIdDb.setGlobalId(globalId);
						globalIdDb.setInventoryName(this.inventoryName);
						globalIdDb.setLocalId(localId);
						globalIdRepo.save(globalIdDb);
					} else {
						this.senError("SENDER_ERROR_INSERTING_INVALID_RESPONSE").describe("couldn't do insert in geoserver, geoserver didn't response any valid id").foundErr();
						log.error("send@GeoserverSender - {} - couldn't do insert in geoserver, geoserver didn't response any valid id, response from geoserver :: {}, xml sent :: {}",
								this.connectorName, bodyResp, xml);
					}

				} catch (Exception e) {
					this.senError("SENDER_ERROR_INSERTING_INVALID_RESPONSE").describe("couldn't read response xml in transaction insert").foundErr().exception(e);
					log.error("send@GeoserverSender - {} - couldn't read response xml {} in transaction insert, exception ", this.connectorName, bodyResp, e);
				}
			} else if (update) {
				Matcher m = Pattern.compile("<wfs:totalUpdated>1</wfs:totalUpdated>").matcher(bodyResp);
				if (!m.find()) {
					this.senError("SENDER_ERROR_UPDATING_INVALID_RESPONSE").describe("couldn't do update transaction").foundErr(globalId);
					
					log.error(
							"send@GeoserverSender - {} - couldn't do update transaction, response from geoserver :: {}, xml sent :: {}",
							this.connectorName, bodyResp, xml);
				}
			} else if (delete) {
				globalIdRepo.delete(globalIdDbOpt.get());
				Matcher m = Pattern.compile("<wfs:totalDeleted>1</wfs:totalDeleted>").matcher(bodyResp);
				
				if (!m.find()) {
					this.senError("SENDER_ERROR_DELETING_INVALID_RESPONSE").describe("couldn't do delete transaction").foundErr(globalId);
					
					log.error(
							"send@GeoserverSender - {} - couldn't do delete transaction, response from geoserver :: {}, xml sent :: {}",
							this.connectorName, bodyResp, xml);
				}
			}
		} catch (Exception e) {
			log.error("send@GeoserverSender - {} - exception while creating xml", this.connectorName, e);
			this.senError("SENDER_GLOBAL_EXCEPTION").describe("An fatal exception has occurred when creating/sending to geoserver").foundErr(globalId).exception(e);
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
	public String sendPostXML(String uri, String xml, Integer retries) throws Exception {

		Instant start = Instant.now();
		try {

			log.debug("sendPostMXL@GeoserverSender - {} - init with uri '{}' xml '{}' and timeout '{}'", this.connectorName, uri, xml, timeout);

			int connectTimeout = timeout;
			int socketTimeout = timeout;

			HttpPost httpPost = new HttpPost(uri);
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
					.setSocketTimeout(socketTimeout).build();
			String authType = auth;
			String authHeader = "";
			if (authType != null && authType.equals("Basic")) {
				String auth = username + ":" + password;
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
				authHeader = "Basic " + new String(encodedAuth);
			}
			httpPost.setConfig(requestConfig);
			httpPost.setEntity(new StringEntity(xml, HTTP.UTF_8));
			httpPost.setHeader("Accept", "application/xml");
			httpPost.setHeader("Content-type", "application/xml");
			httpPost.setHeader("Authorization", authHeader);
			HttpResponse resp = HttpClientBuilder.create().build().execute(httpPost);
			StringBuilder sb = new StringBuilder();

			try (InputStreamReader inputStreamReader = new InputStreamReader(resp.getEntity().getContent())) {
				BufferedReader br = new BufferedReader(inputStreamReader);
				String readLine;
				while (((readLine = br.readLine()) != null)) {
					sb.append("\n").append(readLine);
				}
			}

			String ret = sb.toString();

			if (log.isDebugEnabled()) {
				log.debug("sendPostMXL@GeoserverSender - {} - response '{}'", this.connectorName, ret);
			}

			return ret;

		} catch (SocketTimeoutException e) {
			log.error("sendPostXML@GeoserverSender - {} - Timeout sending xml to '{}' readTimeout '{}' with xml '{}'", this.connectorName, uri,
					timeout, xml);
			if (retries != 0 && !this.getInstance().shouldEnd()) {
				log.info("sendPostXML@GeoserverSender - {} - retrying operation for {} time", this.connectorName, retries);
				sendPostXML(uri, xml, retries--);
			} else {
				this.senError("SENDER_TIMEOUT").describe("Timeout in sending petition to geoserver").foundErr().exception(e);
			}
			throw e;
		} catch (Exception e) {
			log.error("sendPostXML@GeoserverSender - {} - Error sending xml to '{}' with xml '{}'.", this.connectorName, uri, xml, e);
			throw e;
		}
	}

}
