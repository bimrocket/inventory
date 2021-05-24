package cat.santfeliu.api.dto;

import java.util.ArrayList;
import java.util.List;

public class ConnectorComponentDTO {

	private String connectorComponentName;
	private String connectorComponentType;
	private String connectorComponentClass;
	private List<ComponentConfigKeyDTO> componentConfigKeys = new ArrayList<>();
	
	public ConnectorComponentDTO() {
		
	}

	public ConnectorComponentDTO(String componentName, String componentType, String componentClass) {
		this.connectorComponentName = componentName;
		this.connectorComponentType = componentType;
		this.connectorComponentClass = componentClass;
	}

	public String getConnectorComponentName() {
		return connectorComponentName;
	}

	public void setConnectorComponentName(String connectorComponentName) {
		this.connectorComponentName = connectorComponentName;
	}

	public String getConnectorComponentType() {
		return connectorComponentType;
	}

	public void setConnectorComponentType(String connectorComponentType) {
		this.connectorComponentType = connectorComponentType;
	}

	public String getConnectorComponentClass() {
		return connectorComponentClass;
	}

	public void setConnectorComponentClass(String connectorComponentClass) {
		this.connectorComponentClass = connectorComponentClass;
	}

	public List<ComponentConfigKeyDTO> getComponentConfigKeys() {
		return componentConfigKeys;
	}

	public void setComponentConfigKeys(List<ComponentConfigKeyDTO> componentConfigKeys) {
		this.componentConfigKeys = componentConfigKeys;
	}
	
	

	

}
