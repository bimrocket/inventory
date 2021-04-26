package cat.santfeliu.api.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import cat.santfeliu.api.beans.ResponseWfsContainer;

@Service
public class GeoserverJobService {

	private static final Logger log = LoggerFactory.getLogger(GeoserverJobService.class);

	public ResponseWfsContainer getWfsFeatures(String uri, String username, String password, String format,
			String params, String capa) {

		Gson g = new Gson();
		HttpClient client = HttpClients.custom().build();
		String auth = username + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		HttpUriRequest request = RequestBuilder.get()
				.setUri(uri + params + "&typeName=" + capa + "&outputFormat=" + format)
				.setHeader(HttpHeaders.AUTHORIZATION, authHeader).build();

		ResponseWfsContainer toReturn = new ResponseWfsContainer();
		HttpResponse response;
		boolean invalidJson = false;
		try {
			response = client.execute(request);
			try {

				String bodyResp = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
				toReturn.setResponse(bodyResp);
				toReturn.setHttpStatus(200);
				log.debug("getWfsFeatures@GeoserverJobs - respone ok, found features");

			} catch (Exception e) {
				invalidJson = true;
				log.debug("getWfsFeatures@GeoserverJobs - get wfs features failed, found 0 features");
				log.debug("getWfsFeatures@GeoserverJobs - exception in parsing json {} ", e.getCause(), e);
			}
		} catch (IOException e) {
			invalidJson = true;
			log.debug("getWfsFeatures@GeoserverJobs - get wfs features failed, found 0 features");
			log.debug("getWfsFeatures@GeoserverJobs - exception in sending petition {} ", e.getCause(), e);
		}
		if (invalidJson) {
			String json = "{ \"features\": [], \"type\": \"FeatureCollection\" }";
			toReturn.setResponse(json);
			toReturn.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return toReturn;
	}
}
