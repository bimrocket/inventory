package cat.santfeliu.api.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cat.santfeliu.api.beans.SentinelError;
import cat.santfeliu.api.service.ConnectorRunnerService;
import cat.santfeliu.api.utils.ConfigContainer;
import cat.santfeliu.api.utils.ConfigManager;
import cat.santfeliu.api.utils.InventoryErrorSentinelUtils;

public abstract class ConnectorComponent {

	private static final Logger log = LoggerFactory.getLogger(ConnectorComponent.class);
	protected String inventoryName;
	protected String connectorName;
	protected Integer executionId;
	protected ConfigContainer params;
	
	@Autowired
	protected InventoryErrorSentinelUtils sentinel;

	/**
	 * Initialization of connector component
	 */
	public void init(String inventoryName, String connectorName, Integer executionId, ConfigContainer params) {
		this.inventoryName = inventoryName;
		this.connectorName = connectorName;
		this.params = params;
		this.executionId = executionId;
		ConfigManager.inject(this, params);
		log.debug("init@ConnectorComponent - initialization of connector component with name {} for connector {}", inventoryName, connectorName);
	}
	
	public SentinelError senError(String errorKey) {
		return sentinel.senError(this.connectorName, this.executionId, errorKey, 4);
	}
	
	public void destroy() {
		this.params = null;
	}
	
	public ConnectorInstance getInstance() {
		return ConnectorRunnerService.instances.get(this.connectorName);
	}
}
