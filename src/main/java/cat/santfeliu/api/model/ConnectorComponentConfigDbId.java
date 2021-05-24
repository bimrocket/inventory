package cat.santfeliu.api.model;

import java.io.Serializable;

public class ConnectorComponentConfigDbId implements Serializable {
    java.lang.String configKey;
    java.lang.String connectorName;
    java.lang.String componentType;
    
    public ConnectorComponentConfigDbId() {
    	
    }
    
	public ConnectorComponentConfigDbId(String configKey, String connectorName, String componentType) {
		this.configKey = configKey;
		this.connectorName = connectorName;
		this.componentType = componentType;
	}
	
	public java.lang.String getConfigKey() {
		return configKey;
	}
	public void setConfigKey(java.lang.String configKey) {
		this.configKey = configKey;
	}
	public java.lang.String getConnectorName() {
		return connectorName;
	}
	public void setConnectorName(java.lang.String connectorName) {
		this.connectorName = connectorName;
	}
	public java.lang.String getComponentType() {
		return componentType;
	}
	public void setComponentType(java.lang.String componentType) {
		this.componentType = componentType;
	}
    
    
}
