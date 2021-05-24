package cat.santfeliu.api.components;

import cat.santfeliu.api.model.ConnectorDb;

public class ConnectorInstance {

	private ConnectorDb connector;
	private ConnectorLoader connectorLoader;
	private ConnectorTransformer connectorTransformer;
	private ConnectorSender connectorSender;

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

}
