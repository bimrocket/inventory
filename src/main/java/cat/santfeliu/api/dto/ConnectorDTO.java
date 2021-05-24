package cat.santfeliu.api.dto;

import java.util.List;

public class ConnectorDTO {

	private String connectorName;
	private String connectorLoader;
	private String connectorTransformer;
	private String connectorSender;
	private String inventoryName;
	private List<ConnectorParamDTO> connectorParams;

	public ConnectorDTO() {
	
	}

	public ConnectorDTO(String connectorName, String connectorLoader, String connectorTransformer,
			String connectorSender, List<ConnectorParamDTO> connectorParams) {
		this.connectorName = connectorName;
		this.connectorLoader = connectorLoader;
		this.connectorTransformer = connectorTransformer;
		this.connectorSender = connectorSender;
		this.connectorParams = connectorParams;
	}

	public String getConnectorName() {
		return connectorName;
	}

	public void setConnectorName(String connectorName) {
		this.connectorName = connectorName;
	}

	public String getConnectorLoader() {
		return connectorLoader;
	}

	public void setConnectorLoader(String connectorLoader) {
		this.connectorLoader = connectorLoader;
	}

	public String getConnectorTransformer() {
		return connectorTransformer;
	}

	public void setConnectorTransformer(String connectorTransformer) {
		this.connectorTransformer = connectorTransformer;
	}

	public String getConnectorSender() {
		return connectorSender;
	}

	public void setConnectorSender(String connectorSender) {
		this.connectorSender = connectorSender;
	}

	public List<ConnectorParamDTO> getConnectorParams() {
		return connectorParams;
	}

	public void setConnectorParams(List<ConnectorParamDTO> connectorParams) {
		this.connectorParams = connectorParams;
	}

	public String getInventoryName() {
		return inventoryName;
	}

	public void setInventoryName(String inventoryName) {
		this.inventoryName = inventoryName;
	}
	
	

}
