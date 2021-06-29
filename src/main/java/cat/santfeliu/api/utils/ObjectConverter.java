package cat.santfeliu.api.utils;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;


/**
 *
 * @author realor
 */
public abstract class ObjectConverter {
	protected final ConfigContainer config;

	public ObjectConverter(ConfigContainer config) {
		this.config = config;
	}

	public abstract JsonNode convert(JsonNode object);

	public void end() throws Exception {
	}

	public void begin(Map<String, String> scopeObjects) throws Exception {
	}
}