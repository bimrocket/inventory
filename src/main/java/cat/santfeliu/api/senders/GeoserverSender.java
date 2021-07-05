package cat.santfeliu.api.senders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
import cat.santfeliu.api.enumerator.GeoserverSenderConfigKeys;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.utils.GeometryXMLUtils;

public class GeoserverSender extends ConnectorSender {

	private static final Logger log = LoggerFactory.getLogger(GeoserverSender.class);
	ObjectMapper mapper = new ObjectMapper();

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
			log.debug("send@GeoserverSender - add insert of geoserversender key {}",
					this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_LAYER.getKey()));
			dir.add("Insert").add(this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_LAYER.getKey()));
			dir.attr("xmlns", "https://www.santfeliu.cat");
			Iterator<String> keys = elem.fieldNames();
			while (keys.hasNext()) {
				String key = keys.next();
				
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
						log.error("send@GeoserverSender - can't map geom object as jsonObject {}", elem.get(key).asText());
					}
					log.debug("send@GeoserverSender - add geom key");
					dir.add("geom");
					JsonNode geomObj = elemElem;
					if (geomObj != null && !geomObj.isNull()) {
						GeometryXMLUtils.putGeometryObjInXML(dir, geomObj);
					}
					dir.up();
				} else {
					JsonNode elemElem = null;
					elemElem = elem.get(key);
					dir.add(key);
					log.debug("send@GeoserverSender - add not geom key {}", key);
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
			log.debug("send@GeoserverSender - add update of geoserversender key {}",
					this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_TYPE_NAME.getKey()));
			dir.add("Update").attr("typeName",
					this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_TYPE_NAME.getKey()));
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
						log.error("send@GeoserverSender - can't map geom object as jsonObject {}", elem.get(key).asText());
					}
					log.debug("send@GeoserverSender - set geom key");
					dir.add("Name").set("geom").up();
					JsonNode geomObj = elemElem;
					dir.add("Value");

					GeometryXMLUtils.putGeometryObjInXML(dir, geomObj);
					
					dir.up();
				} else {
					JsonNode elemElem = null;
					elemElem = elem.get(key);

					log.debug("send@GeoserverSender - set another key {}", key);
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
			log.debug("send@GeoserverSender - add delete of geoserversender key {}",
					this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_TYPE_NAME.getKey()));
			dir.add("Delete").attr("typeName",
					this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_TYPE_NAME.getKey()));
			dir.attr("xmlns:sf", "https://www.santfeliu.cat");
			dir.add("Filter").attr("xmlns", "http://www.opengis.net/ogc");
			dir.add("FeatureId").attr("fid", localId);
		}
		try {
			String xml = new Xembler(dir).xml().replaceAll("\r\n", "");

//			StringReader reader = new StringReader(xml);
//			Parser parser = new Parser(new WFSConfiguration());
//			TransactionType tt = (TransactionType) parser.parse(reader);
//			InsertElementType insert1 = (InsertElementType) tt.getInsert().get(0);
//			insert1
			log.info("xml done :: {}", xml);
			String uri = this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_URL.getKey());
			String bodyResp = sendPostXML(uri, xml);

			if (insert) {
				// an instance of builder to parse the specified xml file
				try {
					Matcher m = Pattern.compile("fid=\"[A-z_.0-9]+\"").matcher(bodyResp);
					while (m.find()) {
						localId = m.group(0).replaceAll("fid=", "").replaceAll("\"", "");
					}
					GlobalIdDb globalIdDb = new GlobalIdDb();
					globalIdDb.setGlobalId(globalId);
					globalIdDb.setInventoryName(this.inventoryName);
					globalIdDb.setLocalId(localId);
					globalIdRepo.save(globalIdDb);
				} catch (Exception e) {
					log.error("send@GeoserverSender - couldn't read response xml in transaction insert, exception ", e);
				}
			} else if (update) {
				Matcher m = Pattern.compile("<wfs:totalUpdated>1</wfs:totalUpdated>").matcher(bodyResp);
				if (!m.find()) {
					log.error(
							"send@GeoserverSender - couldn't do update transaction, response from geoserver :: {}, xml sent :: {}",
							bodyResp, xml);
				}
			} else if (delete) {
				globalIdRepo.delete(globalIdDbOpt.get());
				Matcher m = Pattern.compile("<wfs:totalDeleted>1</wfs:totalDeleted>").matcher(bodyResp);
				if (!m.find()) {
					log.error(
							"send@GeoserverSender - couldn't do delete transaction, response from geoserver :: {}, xml sent :: {}",
							bodyResp, xml);
				}
			}
		} catch (Exception e) {
			log.error("send@GeoserverSender - exception while creating xml", e);
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
	public String sendPostXML(String uri, String xml) throws Exception {

		Instant start = Instant.now();
		try {

			log.debug("sendPostMXL@GeoserverSender - init with uri '{}' xml '{}'", uri, xml);

			int connectTimeout = 60000;
			int socketTimeout = 60000;

			HttpPost httpPost = new HttpPost(uri);
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
					.setSocketTimeout(socketTimeout).build();
			String authType = this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_AUTH.getKey());
			String authHeader = "";
			if (authType != null && authType.equals("Basic")) {
				String auth = this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_USERNAME.getKey()) + ":"
						+ this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_PASSWORD.getKey());
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
				log.debug("sendPostMXL@GeoserverSender - response '{}'", ret);
			}

			return ret;

		} catch (SocketTimeoutException e) {
			log.error("sendPostMXL@GeoserverSender - Error sending xml to '{}' readTimeout '{}' with xml '{}'", uri,
					60000, xml);
			throw e;
		} catch (UnsupportedEncodingException e) {
			log.error("sendPostMXL@GeoserverSender - Error sending xml to '{}' with xml '{}'.", uri, xml, e);
			throw e;
		} catch (ClientProtocolException e) {
			log.error("sendPostMXL@GeoserverSender - Error sending xml to '{}' with xml '{}'.", uri, xml, e);
			throw e;
		} catch (IOException e) {
			log.error("sendPostMXL@GeoserverSender - Error sending xml to '{}' with xml '{}'.", uri, xml, e);
			throw e;
		} catch (Exception e) {
			log.error("sendPostMXL@GeoserverSender - Error sending xml to '{}' with xml '{}'.", uri, xml, e);
			throw e;
		}
	}

}
