package cat.santfeliu.api.enumerator;

public enum GeoserverSenderConfigKeys {
	GEOSERVER_URL("url", "Url fins wfs de servidor geoserver", true),
	GEOSERVER_LAYER("layer", "Layer a actualitza", true),
	GEOSERVER_TYPE_NAME("type.name", "Nom de entitat a actualitza", true),
	GEOSERVER_AUTH("auth", "Authentication used currently supported only Basic", false),
	GEOSERVER_USERNAME("username", "User used for basic authentication", false),
	GEOSERVER_PASSWORD("password", "Password used for basic authentication", false);
	
	private String key;
	private String description;
	private boolean required;

	private GeoserverSenderConfigKeys(String key, String description, boolean required) {
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
