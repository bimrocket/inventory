package cat.santfeliu.api.enumerator;

public enum ComponentEnum {

	GEOSERVER_LOADER("GeoserverLoader", ComponentTypeEnum.LOADER.getName()),
	JSON_KAFKA_LOADER("JSONKafkaLoader", ComponentTypeEnum.LOADER.getName()),
	RHINO_TRANSFORMER("RhinoTransformer", ComponentTypeEnum.TRANSFORMER.getName()),
	GEOSERVER_SENDER("GeoserverSender", ComponentTypeEnum.SENDER.getName()),
	JSON_KAFKA_SENDER("JSONKafkaSender", ComponentTypeEnum.SENDER.getName()),
	LOG_SENDER("GeoserverSender", ComponentTypeEnum.SENDER.getName());
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
