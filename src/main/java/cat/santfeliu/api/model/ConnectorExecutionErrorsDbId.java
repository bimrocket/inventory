// Generated with g9.

package cat.santfeliu.api.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

public class ConnectorExecutionErrorsDbId implements Serializable {

    private String errorPlace;
    private String errorKey;
    private Integer executionId;


    /** Default constructor. */
    public ConnectorExecutionErrorsDbId() {
        super();
    }


	public String getErrorPlace() {
		return errorPlace;
	}


	public void setErrorPlace(String errorPlace) {
		this.errorPlace = errorPlace;
	}
	
	public ConnectorExecutionErrorsDbId errorPlace(String errorPlace) {
		this.errorPlace = errorPlace;
		return this;
	}


	public String getErrorKey() {
		return errorKey;
	}


	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}
	
	
	public ConnectorExecutionErrorsDbId errorKey(String errorKey) {
		this.errorKey = errorKey;
		return this;
	}



	public Integer getExecutionId() {
		return executionId;
	}


	public void setExecutionId(Integer executionId) {
		this.executionId = executionId;
	}
	
	
	public ConnectorExecutionErrorsDbId executionId(Integer executionId) {
		this.executionId = executionId;
		return this;
	}
    
    
}
