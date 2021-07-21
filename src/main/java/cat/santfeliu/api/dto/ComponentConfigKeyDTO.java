package cat.santfeliu.api.dto;

public class ComponentConfigKeyDTO {

	private String configKey;
	private String description;
	private boolean required;
	private boolean hidden;
	
	public ComponentConfigKeyDTO() {
		
	}
	
	

	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
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



	public boolean isHidden() {
		return hidden;
	}



	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	
	
	
	
}
