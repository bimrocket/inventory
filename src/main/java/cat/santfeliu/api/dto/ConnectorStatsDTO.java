package cat.santfeliu.api.dto;

import java.util.Date;

import javax.persistence.Column;

public class ConnectorStatsDTO {
	private int executionId;
    private Date executionStartDate;
    private Date executionEndDate;
    private Integer executionObjectsLoaded;
    private Integer executionObjectsTransformed;
    private Integer executionObjectsSent;
    private Integer executionObjectsDeleted;
    private String executionFinalState;
    private String executionConnectorName;
    
	public ConnectorStatsDTO() {
		
	}
	
	public int getExecutionId() {
		return executionId;
	}
	public void setExecutionId(int executionId) {
		this.executionId = executionId;
	}
	public Date getExecutionStartDate() {
		return executionStartDate;
	}
	public void setExecutionStartDate(Date executionStartDate) {
		this.executionStartDate = executionStartDate;
	}
	public Date getExecutionEndDate() {
		return executionEndDate;
	}
	public void setExecutionEndDate(Date executionEndDate) {
		this.executionEndDate = executionEndDate;
	}
	public Integer getExecutionObjectsLoaded() {
		return executionObjectsLoaded;
	}
	public void setExecutionObjectsLoaded(int executionObjectsLoaded) {
		this.executionObjectsLoaded = executionObjectsLoaded;
	}
	public Integer getExecutionObjectsTransformed() {
		return executionObjectsTransformed;
	}
	public void setExecutionObjectsTransformed(int executionObjectsTransformed) {
		this.executionObjectsTransformed = executionObjectsTransformed;
	}
	public Integer getExecutionObjectsSent() {
		return executionObjectsSent;
	}
	public void setExecutionObjectsSent(int executionObjectsSent) {
		this.executionObjectsSent = executionObjectsSent;
	}
	public Integer getExecutionObjectsDeleted() {
		return executionObjectsDeleted;
	}
	public void setExecutionObjectsDeleted(int executionObjectsDeleted) {
		this.executionObjectsDeleted = executionObjectsDeleted;
	}
	public String getExecutionFinalState() {
		return executionFinalState;
	}
	public void setExecutionFinalState(String executionFinalState) {
		this.executionFinalState = executionFinalState;
	}
	public String getExecutionConnectorName() {
		return executionConnectorName;
	}
	public void setExecutionConnectorName(String executionConnectorName) {
		this.executionConnectorName = executionConnectorName;
	}
    
    
}
