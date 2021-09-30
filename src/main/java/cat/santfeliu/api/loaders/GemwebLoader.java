package cat.santfeliu.api.loaders;

import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import cat.santfeliu.api.utils.ConfigProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
import com.jayway.jsonpath.JsonPath;

import cat.santfeliu.api.beans.LoaderJsonObject;
import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.exceptions.ApiErrorException;
import cat.santfeliu.api.model.GlobalIdDb;
import net.minidev.json.JSONArray;

public class GemwebLoader extends ConnectorLoader {

    private static final Logger log = LoggerFactory.getLogger(GemwebLoader.class);

    @ConfigProperty(name = "url", description = "Gemweb url")
    private String url;

    @ConfigProperty(name = "client.id", description = "Used to obtain token")
    private String clientId;

    @ConfigProperty(name = "client.secret", description = "Secret used for authentication")
    private String clientSecret;

    @ConfigProperty(name = "category",description = "Category to load from gemweb")
    private String category;

    @ConfigProperty(name = "auth", description = "Authentication used currently supported only Bearer")
    private String auth;

    @ConfigProperty(name = "json.path.local.id", description = "JSON Path to local id of processed element")
    private String jsonPathLocalId;

    @ConfigProperty(name = "json.path.elements", description = "Json Path to array which contains elements to process")
    private String jsonPathElementArray;

    @ConfigProperty(name = "filter.field", description = "Used in partial load based on update date check, if indicated should be field corresponding to incoming element update date", required = false)
    private String filterField;

    @ConfigProperty(name = "filter.format.date", description = "Filter field format of object updated date, if indicated should be parseable by Java SimpleDateFormat", required = false)
    private String filterFormat;

    @ConfigProperty(name = "has.end", description = "Defines whether objects are arriving endlessly or there is a defined number of objects")
    protected boolean hasEnd;

    @ConfigProperty(name = "sleep.time", description = "Whenever the thread sleeps in milliseconds if no object is found (= null), it is used only if the loader has no end")
    protected int sleep_time;

    @ConfigProperty(name = "load.timeout", description = "Timeout for loading objects, for example in case of geoserver is the maximum that takes the request")
    protected int timeout;

    @Override
    public JsonNode load(long timeout) {
        if (loaded == null && this.reset) {
            this.checkedDeletions = false;
            this.reset = false;
            try {
                log.debug("load@GemwebLoader - {} - load response of getTokenAcces {}", this.connectorName, this.getAccessToken());
                return loadResponse(this.getAccessToken());
            } catch (Exception e) {
                return null;
            }
        } else {
            if (this.checkedDeletions) {
                log.debug("load@GemwebLoader - {} - return checked deletion", this.connectorName);
                return this.getDeletion();
            } else {

                JsonNode obj = getObject();
                if (obj == null) {
                    this.checkDeletions();
                    log.debug("load@GemwebLoader - {} - return not checked deletion", this.connectorName);
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
            log.debug("getObject@GemwebLoader - {} - get :: {}", this.connectorName, mapper.writeValueAsString(toReturn));
        } catch (IndexOutOfBoundsException e) {
            // End of objects if loop has no end we put loaded as null so next time (after
            // sleep) it
            // recover all objects again
            return null;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (toReturn != null) {
            toReturn = transformForTransformer(toReturn);
            try {
                log.debug("getObject@GemwebLoader - {} - getAsJsonObject :: {}",  this.connectorName, mapper.writeValueAsString(toReturn));
            } catch (JsonProcessingException e) {
            	log.error("getObject@GemwebLoader - {} - ", this.connectorName, e);
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
            log.debug("transformForTransformer@GemwebLoader - {} - globalId {} not treated add to treatedGUIDs", this.connectorName, globalId);
            return mapper.valueToTree(loaderJson);
        } else {
            return null;
        }

    }

    /**
     * Send a POST request.
     *
     * @param uri Third party server
     * @param xml XML to send
     * @return XML to return
     * @throws Exception
     */
    public JsonNode loadResponse(String token) throws Exception {

        Instant start = Instant.now();

        String uri = url;

        try {
            int connectTimeout = (int) this.loadTimeout;
            int socketTimeout = (int) this.loadTimeout;

            log.debug("loadResponse@GemwebLoader - {} - init with uri '{}' and token '{}'", this.connectorName, uri, token);
            var httpPost = new HttpPost(uri);
            var requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
                    .setSocketTimeout(socketTimeout).build();
            String authType = auth;
            var authHeader = "";
            if (authType != null && authType.equals("Bearer")) {
                authHeader = "Bearer " + new String(token);
                httpPost.setHeader("Authorization", authHeader);
            }
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("request", "get_inventory"));
            nvps.add(new BasicNameValuePair("access_token", token));
            nvps.add(new BasicNameValuePair("category", category));

            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            httpPost.setConfig(requestConfig);
            httpPost.setHeader("Accept", "application/xml");
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            HttpResponse resp = HttpClientBuilder.create().build().execute(httpPost);

            String ret = inputStreamResponseToString(resp.getEntity().getContent());
            String json = XML.toJSONObject(ret).toString();

            try {
                JSONArray array  = JsonPath.parse(json).<JSONArray>read(jsonPathElementArray);
                all = mapper.valueToTree(array);
                ArrayNode arrayFiltered = mapper.createArrayNode();
                Date updateDate = null;
                if (this.page.getContent().isEmpty() || this.page.getContent().size() < 2 || filterField == null) {
                    this.loaded = all;

                    for (JsonNode e : all) {
                        Pair<String, String> ids = getIds(e);
                        String localId = ids.getLeft();
                        String globalId = ids.getRight();

                        this.guidsExisting.put(localId, globalId);
                        this.allGUIDs.add(globalId);
                    }
                } else {
                    updateDate = this.page.getContent().get(1).getExecutionStartDate();
                    SimpleDateFormat sdf = new SimpleDateFormat(filterFormat);

                    log.debug("loadResponse@GemwebLoader - {} - filter all pair of guid to return", this.connectorName);
                    if(all.isArray()) {
                        for (JsonNode e : all ) {
                            Pair<String, String> ids = getIds(e);
                            String localId = ids.getLeft();
                            String globalId = ids.getRight();

                            this.guidsExisting.put(localId, globalId);
                            this.allGUIDs.add(globalId);
                            String dateUpdateElemStr = JsonPath.parse(mapper.writeValueAsString(e)).<String>read(filterField);
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
                    log.debug("loadResponse@GemwebLoader - {} - sort all of global id", this.connectorName);
                    String[] arr = this.allGUIDs.toArray(new String[this.allGUIDs.size()]);
                    Arrays.sort(arr);
                    List<String> list = new ArrayList<>();
                    Collections.addAll(list, arr);
                    this.allGUIDs = list;
                    this.loaded = arrayFiltered;
                }
                return getObject();
            } catch (Exception e) {
            	this.senError("LOADER_LOAD_EXCEPTION").describe("Loader has found a fatal exception while treating objects").foundErr().exception(e);
                log.error("loadResponse@GemwebLoader - {} - exception while sending petition : ", this.connectorName,  e);
                throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "exception while sending petition to gemweb", e.getMessage());

            }

        } catch (SocketTimeoutException e) {
        	this.senError("LOADER_TIMEOUT_EXCEPTION").describe("Loader timedout while retriving objects").foundErr().exception(e);
            log.error("loadResponse@GemwebLoader - {} - Error sending POST to '{}' readTimeout '{}' with xml", this.connectorName, uri, 60000);
            throw e;
        } catch (Exception e) {
        	this.senError("LOADER_GLOBAL_EXCEPTION").describe("Loader has found a fatal exception while retriving objects").foundErr().exception(e);
            log.error("loadResponse@GemwebLoader - {} - Error sending POST to '{}'.", this.connectorName, uri, e);
            throw e;
        }
    }

    /**
     * Send a POST request.
     *
     * @param uri Third party server
     * @param xml XML to send
     * @return XML to return
     * @throws Exception
     */
    public String getAccessToken() throws Exception {

        Instant start = Instant.now();

        String uri = url;

        try {
            int connectTimeout = (int) this.loadTimeout;
            int socketTimeout = (int) this.loadTimeout;

            log.debug("getAccessToken@GemwebLoader - {} - init with uri '{}'", this.connectorName, uri);
            HttpPost httpPost = new HttpPost(uri);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
                    .setSocketTimeout(socketTimeout).build();
            
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("request", "get_token"));
            nvps.add(new BasicNameValuePair("client_id", clientId));
            nvps.add(new BasicNameValuePair("client_secret", clientSecret));
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
                log.debug("getAccessToken@GemwebLoader - {} - response '{}'", this.connectorName, ret);
            }

            if (node.get("error") != null) {
                log.error("getAccessToken@GemwebLoader - {} - error in authentication, error responded : {}",
                		this.connectorName,
                        node.get("resultat").get("error"));
            }
            JsonNode resultat = node.get("access_token");
            if (resultat != null) {
                accessToken = resultat.asText();
            }

            return accessToken;

        } catch (SocketTimeoutException e) {
        	this.senError("LOADER_TIMEOUT_EXCEPTION_ACCESS_TOKEN").describe("Timeout in retriving access token").foundErr().exception(e);
            log.error("getAccessToken@GemwebLoader - {} - Error sending POST to '{}' readTimeout '{}' with xml", this.connectorName, uri, 60000);
            throw e;
        } catch (Exception e) {
        	this.senError("LOADER_GLOBAL_EXCEPTION_ACCESS_TOKEN").describe("Fatal exception in retriving access token").foundErr().exception(e);
            log.error("getAccessToken@GemwebLoader - {} - Error sending POST to '{}'.", this.connectorName, uri, e);
            throw e;
        }
    }

    private String inputStreamResponseToString(InputStream reader) {
        StringBuilder sb = new StringBuilder();

        log.debug("inputStreamResponseToString@GemwebLoader - {} - reading response of input", this.connectorName);
        try (InputStreamReader inputStreamReader = new InputStreamReader(reader)) {
            BufferedReader br = new BufferedReader(inputStreamReader);
            String readLine;
            while (((readLine = br.readLine()) != null)) {
                sb.append("\n").append(readLine);
            }
        } catch (IOException e) {
            log.error("inputStreamResponseToString@GemwebLoader - {} -  Error reading response", this.connectorName, e);
            this.senError("LOADER_STREAM_ERROR").describe("Error reading response stream").foundErr().exception(e);
        }

        String ret = sb.toString().replaceAll("\r\n", "").replaceAll("\t", "").replaceAll("\n", "");
        return ret;
    }

    private Pair<String, String> getIds(JsonNode e) {
        String localId = null;
        try {
            localId = JsonPath.parse(mapper.writeValueAsString(e)).read(jsonPathLocalId).toString();

        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }
        String globalId = null;

        String hasGuid = this.guidsExisting.get(localId);
        log.debug("getIds@GeoserverLoader - {} - has guid {}, local id :: {}, guids size {}", this.connectorName, hasGuid != null, localId,
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
