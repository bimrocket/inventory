package cat.santfeliu.api.dto;

import java.util.Date;

import javax.persistence.Column;

public class ConnectorErrorsDTO {
	private int executionId;
    private String errorPlace;
    private String errorDescription;
    private String errorGlobalIds;
    private String errorException;
    private String errorKey;
    private Integer errorOccurrences;
    private String executionConnectorName;
    
	public ConnectorErrorsDTO() {
		
	}
	
	public int getExecutionId() {
		return executionId;
	}
	public void setExecutionId(int executionId) {
		this.executionId = executionId;
	}
	
	public String getErrorPlace() {
		return errorPlace;
	}

	public void setErrorPlace(String errorPlace) {
		this.errorPlace = errorPlace;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public String getErrorGlobalIds() {
		return errorGlobalIds;
	}

	public void setErrorGlobalIds(String errorGlobalIds) {
		this.errorGlobalIds = errorGlobalIds;
	}

	public String getErrorException() {
		return errorException;
	}

	public void setErrorException(String errorException) {
		this.errorException = errorException;
	}

	public String getErrorKey() {
		return errorKey;
	}

	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}

	public Integer getErrorOccurrences() {
		return errorOccurrences;
	}

	public void setErrorOccurrences(Integer errorOccurrences) {
		this.errorOccurrences = errorOccurrences;
	}

	public String getExecutionConnectorName() {
		return executionConnectorName;
	}
	public void setExecutionConnectorName(String executionConnectorName) {
		this.executionConnectorName = executionConnectorName;
	}
    
    
}
