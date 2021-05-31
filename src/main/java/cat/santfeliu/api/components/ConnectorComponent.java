package cat.santfeliu.api.components;

import cat.santfeliu.api.utils.ConfigContainer;

public abstract class ConnectorComponent {

	protected String inventoryName;
	protected ConfigContainer params;

	/**
	 * Init de componente de connector 
	 */
	public void init(String inventoryName, ConfigContainer params) {
		this.inventoryName = inventoryName;
		this.params = params;
	}
	
	public void destroy() {
		this.params = null;
	}
}
