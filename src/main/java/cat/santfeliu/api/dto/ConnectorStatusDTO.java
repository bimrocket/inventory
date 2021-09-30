package cat.santfeliu.api.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ConnectorStatusDTO {

	private String connectorName;
	private String connectorStatus;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
	private Date connectorStartDate;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
	private Date connectorEndDate;

	public ConnectorStatusDTO() {

	}

	public ConnectorStatusDTO(String connectorName, String connectorStatus, Date connectorStartDate,
			Date connectorEndDate) {
		this.connectorName = connectorName;
		this.connectorStatus = connectorStatus;
		this.connectorStartDate = connectorStartDate;
		this.connectorEndDate = connectorEndDate;
	}

	public String getConnectorName() {
		return connectorName;
	}

	public void setConnectorName(String connectorName) {
		this.connectorName = connectorName;
	}

	public String getConnectorStatus() {
		return connectorStatus;
	}

	public void setConnectorStatus(String connectorStatus) {
		this.connectorStatus = connectorStatus;
	}

	public Date getConnectorStartDate() {
		return connectorStartDate;
	}

	public void setConnectorStartDate(Date connectorStartDate) {
		this.connectorStartDate = connectorStartDate;
	}

	public Date getConnectorEndDate() {
		return connectorEndDate;
	}

	public void setConnectorEndDate(Date connectorEndDate) {
		this.connectorEndDate = connectorEndDate;
	}
	
	

}
