package cat.santfeliu.api.enumerator;

public enum JobSourcesParamsEnum {

	GEOSERVER_URI("geoserver.url"),
	GEOSERVER_FORMAT("geoserver.format"),
	GEOSERVER_USERNAME("geoserver.username"),
	GEOSERVER_PASSWORD("geoserver.password"),
	GEOSERVER_PARAMS("geoserver.params"),
	GEOSERVER_JSON_FEATURES_PATH("geoserver.json.features.path"),
	GEOSERVER_LAYER("geoserver.layer");
	
	private String key;
	
	
	private JobSourcesParamsEnum(String key) {
this.key = key;
	}
	
	public String getParamKey() {
return this.key;
	}
}
