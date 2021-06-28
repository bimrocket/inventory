package cat.santfeliu.api.components;

import cat.santfeliu.api.model.ConnectorDb;
import cat.santfeliu.api.model.ConnectorExecutionStatsDb;
import cat.santfeliu.api.model.ConnectorStatusDb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectorInstance {

	private static final Logger log = LoggerFactory.getLogger(ConnectorInstance.class);
	private ConnectorDb connector;
	private ConnectorLoader connectorLoader;
	private ConnectorTransformer connectorTransformer;
	private ConnectorSender connectorSender;
	private ConnectorExecutionStatsDb connectorStats;
	private ConnectorStatusDb connectorStatus;
	private boolean end = false;

	public ConnectorInstance(ConnectorDb connector, ConnectorLoader loader, ConnectorTransformer transformer, ConnectorSender sender) {
		this.connector = connector;
		this.connectorLoader = loader;
		this.connectorTransformer = transformer;
		this.connectorSender = sender;
	}
	
	public void destroy() {
		this.connectorLoader.destroy();
		this.connectorSender.destroy();
		this.connectorTransformer.destroy();
		log.debug("destroy@ConnectorInstance - destroy of connector instance");
	}

	public ConnectorDb getConnector() {
		return connector;
	}

	public void setConnector(ConnectorDb connector) {
		this.connector = connector;
	}

	public ConnectorLoader getConnectorLoader() {
		return connectorLoader;
	}

	public void setConnectorLoader(ConnectorLoader connectorLoader) {
		this.connectorLoader = connectorLoader;
	}

	public ConnectorTransformer getConnectorTransformer() {
		return connectorTransformer;
	}

	public void setConnectorTransformer(ConnectorTransformer connectorTransformer) {
		this.connectorTransformer = connectorTransformer;
	}

	public ConnectorSender getConnectorSender() {
		return connectorSender;
	}

	public void setConnectorSender(ConnectorSender connectorSender) {
		this.connectorSender = connectorSender;
	}
	
	public ConnectorExecutionStatsDb getConnectorStats() {
		return connectorStats;
	}

	public void setConnectorStats(ConnectorExecutionStatsDb connectorStats) {
		this.connectorStats = connectorStats;
	}

	public ConnectorStatusDb getConnectorStatus() {
		return connectorStatus;
	}

	public void setConnectorStatus(ConnectorStatusDb connectorStatus) {
		this.connectorStatus = connectorStatus;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public void stop() {
		this.end = true;
	}
	
	public boolean shouldEnd() {
		return this.end;
	}

}
