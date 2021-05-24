package cat.santfeliu.api.components;

import cat.santfeliu.api.utils.ConfigContainer;

public abstract class ConnectorComponent {

	protected ConfigContainer params;

	/**
	 * Init de componente de connector 
	 */
	public void init(ConfigContainer params) {
		this.params = params;
	}
	
	public void destroy() {
		this.params = null;
	}
}
