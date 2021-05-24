package cat.santfeliu.api.dto;

public class ComponentConfigKeyDTO {

	private String configKey;
	private boolean required;
	
	public ComponentConfigKeyDTO() {
		
	}

	public String getConfigKey() {
		return configKey;
	}

	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
	
	
	
}
