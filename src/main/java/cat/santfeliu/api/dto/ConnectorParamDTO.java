package cat.santfeliu.api.dto;

public class ConnectorParamDTO {

	private String componentType;
	private String configKey;
	private String configValue;

	public ConnectorParamDTO() {

	}

	public ConnectorParamDTO(String componentType, String configKey, String configValue) {
		this.componentType = componentType;
		this.configKey = configKey;
		this.configValue = configValue;
	}

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public String getConfigKey() {
		return configKey;
	}

	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

}
