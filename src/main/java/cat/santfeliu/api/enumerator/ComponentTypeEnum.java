package cat.santfeliu.api.enumerator;

public enum ComponentTypeEnum {

	LOADER("loader"),
	TRANSFORMER("transformer"),
	SENDER("sender");
	
	private String name;
	
	private ComponentTypeEnum(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
