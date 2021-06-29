package cat.santfeliu.api.components;

import cat.santfeliu.api.service.ConnectorRunnerService;
import cat.santfeliu.api.utils.ConfigContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConnectorComponent {

	private static final Logger log = LoggerFactory.getLogger(ConnectorComponent.class);
	protected String inventoryName;
	protected ConfigContainer params;

	/**
	 * Initialization of connector component
	 */
	public void init(String inventoryName, ConfigContainer params) {
		this.inventoryName = inventoryName;
		this.params = params;
		log.debug("init@ConnectorComponent - initialization of connector component with name {}", inventoryName);
	}
	
	public void destroy() {
		this.params = null;
	}
}
