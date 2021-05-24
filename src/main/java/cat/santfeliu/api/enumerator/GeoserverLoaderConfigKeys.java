package cat.santfeliu.api.enumerator;

public enum GeoserverLoaderConfigKeys {

	LOADER_URL("url", true),
	LOADER_PARAMS("params", true),
	LOADER_USERNAME("username", false),
	LOADER_PASSWORD("password", false),
	LOADER_LAYERS_MULTIPLE("layers.*", true),
	LOADER_FORMAT("format", true),
	LOADER_AUTH("auth", false);
	
	private String key;
	private boolean required;
	
	private GeoserverLoaderConfigKeys(String key, boolean required) {
		this.key = key;
		this.required = required;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public boolean isRequired() {
		return this.required;
	}
}
