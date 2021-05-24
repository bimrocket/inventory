package cat.santfeliu.api.enumerator;

public enum ComponentEnum {

	GEOSERVER_LOADER("GeoserverLoader", ComponentTypeEnum.LOADER.getName()),
	RHINO_TRANSFORMER("RhinoTransformer", ComponentTypeEnum.TRANSFORMER.getName());
	
	private String name;
	private String type;
	
	private ComponentEnum(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return this.name;
	}	
	
	public String getType() {
		return this.type;
	}
}
