package cat.santfeliu.api.enumerator;

public enum GemWebLoaderConfigKeys {

	LOADER_URL("url", "Gemweb url", true), LOADER_CLIENT_ID("client.id", "Used to obtain token", true),
	LOADER_CLIENT_SECRET("client.secret", "Secret used for authentication", false), LOADER_CATEGORY("category","Category to load from gemweb", true),
	LOADER_AUTH("auth", "Authentication used currently supported only Bearer", false),
	LOADER_JSON_PATH_LOCAL_ID("json.path.local.id", "La ruta json path fins local id", true),
	LOADER_JSON_PATH_ELEMENT_ARRAY("json.path.elements", "Json Path to element array", true);

	private String key;
	private String description;
	private boolean required;

	private GemWebLoaderConfigKeys(String key, String description, boolean required) {
		this.key = key;
		this.description = description;
		this.required = required;
	}

	public String getKey() {
		return this.key;
	}

	public String getDescription() {
		return this.description;
	}

	public boolean isRequired() {
		return this.required;
	}
}
