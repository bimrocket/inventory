package cat.santfeliu.api.senders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cat.santfeliu.api.components.ConnectorSender;
import cat.santfeliu.api.enumerator.GeoserverSenderConfigKeys;
import cat.santfeliu.api.model.GlobalIdDb;

public class GeoserverSender extends ConnectorSender {

	private static final Logger log = LoggerFactory.getLogger(GeoserverSender.class);

	@Override
	public void send(JsonObject node) {
		boolean insert = false;
		boolean update = false;
		boolean delete = false;
		String localId = "";
		String globalId = node.get("globalId").getAsString();
		Optional<GlobalIdDb> globalIdDbOpt = globalIdRepo.findByInventoryAndGlobalId(this.inventoryName, globalId);
		if (globalIdDbOpt.isPresent()) {
			if (node.get("element").isJsonNull()) {
				delete = true;
			} else {
				update = true;
			}
			localId = globalIdDbOpt.get().getLocalId();
		} else if (!node.get("element").isJsonNull()) {
			insert = true;
		}
		Directives dir = new Directives();
		dir.add("Transaction").attr("xmlns", "http://www.opengis.net/wfs").attr("service", "WFS")
				.attr("version", "1.1.0").attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
				.attr("xsi:schemaLocation", "http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.1.0/wfs.xsd");
		if (insert) {
			JsonObject elem = node.get("element").getAsJsonObject();
			dir.add("Insert").add(this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_LAYER.getKey()));
			dir.attr("xmlns", "https://www.santfeliu.cat");
			for (Map.Entry<String, JsonElement> entry : elem.entrySet()) {
				String key = entry.getKey();
				JsonElement elemElem = entry.getValue();
				if (key.equals("geom")) {
					dir.add("geom");
					JsonObject geomObj = elemElem.getAsJsonObject();
					String type = geomObj.get("type").getAsString();
					if (type.equals("MultiPoint")) {
						dir.add("Point").attr("xmlns", "http://www.opengis.net/gml");
						dir.add("pos");
						String pointCoordsStr = "";
						boolean first = true;
						JsonArray coords = geomObj.get("coordinates").getAsJsonArray();
						for (int i = 0; i < coords.size(); i++) {
							JsonArray pointCoords = coords.get(i).getAsJsonArray();
							for (int j = 0; j < pointCoords.size(); j++) {
								pointCoordsStr += ((i == 0 && j == 0) ? pointCoords.get(j)
										: (" " + pointCoords.get(j)));
							}
						}
						dir.set(pointCoordsStr);
						dir.up().up().up();

					}
				} else {
					dir.add(key);
					dir.set(entry.getValue().getAsString());
					dir.up();
				}
			}

		} else if (update) {
			JsonObject elem = node.get("element").getAsJsonObject();
			dir.add("Update").attr("typeName", this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_TYPE_NAME.getKey()));
			dir.attr("xmlns:sf", "https://www.santfeliu.cat");
			for (Map.Entry<String, JsonElement> entry : elem.entrySet()) { 
				dir.add("Property");
				String key = entry.getKey();
				JsonElement elemElem = entry.getValue();
				if (key.equals("geom")) {
					dir.add("Name").set("geom").up();
					JsonObject geomObj = elemElem.getAsJsonObject();
					String type = geomObj.get("type").getAsString();
					if (type.equals("MultiPoint")) {
						dir.add("Value");
						dir.add("Point").attr("xmlns", "http://www.opengis.net/gml");
						dir.add("pos");
						String pointCoordsStr = "";
						boolean first = true;
						JsonArray coords = geomObj.get("coordinates").getAsJsonArray();
						for (int i = 0; i < coords.size(); i++) {
							JsonArray pointCoords = coords.get(i).getAsJsonArray();
							for (int j = 0; j < pointCoords.size(); j++) {
								pointCoordsStr += ((i == 0 && j == 0) ? pointCoords.get(j)
										: (" " + pointCoords.get(j)));
							}
						}
						dir.set(pointCoordsStr);
						dir.up().up().up();

					}
				} else {
					dir.add("Name");
					dir.set(key);
					dir.up().add("Value");
					dir.set(entry.getValue().getAsString());
					dir.up();
				}
				dir.up();
			}
			dir.add("Filter").attr("xmlns", "http://www.opengis.net/ogc");			
			dir.add("FeatureId").attr("fid",localId);
		} else {
			dir.add("Delete").attr("typeName", this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_TYPE_NAME.getKey()));
			dir.attr("xmlns:sf", "https://www.santfeliu.cat");
			dir.add("Filter").attr("xmlns", "http://www.opengis.net/ogc");			
			dir.add("FeatureId").attr("fid",localId);
		}
		try {
			String xml = new Xembler(dir).xml().replaceAll("\r\n", "");

			log.info("xml done :: {}", xml);
			String uri = this.params.getParamValue(GeoserverSenderConfigKeys.GEOSERVER_URL.getKey());
			String bodyResp = sendPostXML(uri, xml);

			if (insert) {
				// an instance of builder to parse the specified xml file
				try {
					Matcher m = Pattern.compile("fid=\"[A-z_.0-9]+\"").matcher(bodyResp);
					while (m.find()) {
						localId = m.group(0).replaceAll("fid=","").replaceAll("\"","");
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
					log.error("send@GeoserverSender - couldn't do update transaction, response from geoserver :: {}, xml sent :: {}",bodyResp, xml);
				}
			} else if (delete) {
				globalIdRepo.delete(globalIdDbOpt.get());
				Matcher m = Pattern.compile("<wfs:totalDeleted>1</wfs:totalDeleted>").matcher(bodyResp);
				if (!m.find()) {
					log.error("send@GeoserverSender - couldn't do delete transaction, response from geoserver :: {}, xml sent :: {}",bodyResp, xml);
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
