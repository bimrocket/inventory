package cat.santfeliu.api.enumerator;

public enum GeoserverLoaderConfigKeys {

	LOADER_URL("url", "Geoserver wfs url", true), LOADER_PARAMS("params", "Geoserver url query params", true), LOADER_USERNAME("username", "User used for basic authentication", false),
	LOADER_PASSWORD("password", "Password used for basic authentication", false), LOADER_LAYERS_MULTIPLE("layers.*","Layers to load from geoserver can be multiple like this: layers.01, layers.02...", true), LOADER_FORMAT("format", "Format sent to geoserver", true),
	LOADER_AUTH("auth", "Authentication used currently supported only Basic", false);

	private String key;
	private String description;
	private boolean required;

	private GeoserverLoaderConfigKeys(String key, String description, boolean required) {
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
