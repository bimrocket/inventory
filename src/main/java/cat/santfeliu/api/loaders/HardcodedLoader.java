package cat.santfeliu.api.loaders;

import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.JsonPath;

import cat.santfeliu.api.beans.LoaderJsonObject;
import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.utils.ConfigProperty;

public class HardcodedLoader extends ConnectorLoader {
	// LOG
	private static final Logger log = LoggerFactory.getLogger(HardcodedLoader.class);

	// PARAMS
	@ConfigProperty(name = "json.object", description = "JsonObject to load")
	String json;

	boolean loaded = false;

	@Override
	public JsonNode load(long timeout) {
		try {
			if (!loaded) {
				loaded = true;
				return transformForTransformer(mapper.readTree(json));
			} else {
				return null;
			}
		} catch (JsonProcessingException e) {
			this.senError("TRANSFORMER_INVALID_JSON").describe("invalid json defined as json.object in database configÇ").foundErr().exception(e);
			log.error("load@HardcodedLoader - invalid json defined as json.object in database config, json :: {}, exception ::", json, e);
			return null;
		}
	}

	private JsonNode transformForTransformer(JsonNode node) {

		LoaderJsonObject loaderJson = new LoaderJsonObject();
		loaderJson.setGlobalId(UUID.randomUUID().toString());
		loaderJson.setElement(node);
		try {
			return mapper.readTree(mapper.writeValueAsString(loaderJson));
		} catch (JsonProcessingException e) {
			// bever happens
			return null;
		}
	}

	@Override
	public void stop() {
		this.instance.stop();
	}

}
