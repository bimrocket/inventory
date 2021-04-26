package cat.santfeliu.api.enumerator;

public enum JobSourcesEnum {

	GEOSERVER("geoserver");
	private String source;
	
	private JobSourcesEnum(String source) {
this.source = source;
	}
	
	public String getValue() {
return this.source;
	}
}
