// Generated with g9.

package cat.santfeliu.api.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity(name="connector_execution_errors")
@IdClass(ConnectorExecutionErrorsDbId.class)
public class ConnectorExecutionErrorsDb implements Serializable {

    /** Primary key. */
    protected static final String PK = "ConnectorExecutionErrorsPkConnectorExecutionErrors";

    @Id
    @Column(name="error_place", length=400)
    private String errorPlace;
    @Column(name="error_description", length=2000)
    private String errorDescription;
    @Column(name="error_global_ids", length=2000)
    private String errorGlobalIds = "";
    @Column(name="error_exception", length=2000)
    private String errorExceptionName;
    @Id
    @Column(name="error_key", nullable=false, length=100)
    private String errorKey;
    @Id
    @Column(name="execution_id", nullable=false, precision=10)
    private Integer executionId;
    @Column(name="execution_connector_name", nullable=false, length=600)
    private String executionConnectorName;
    @Column(name="error_occurrences", nullable=false, precision=10)
    private Integer errorOccurrences = 0;


    /** Default constructor. */
    public ConnectorExecutionErrorsDb() {
        super();
    }

    /**
     * Access method for errorPlace.
     *
     * @return the current value of errorPlace
     */
    public String getErrorPlace() {
        return errorPlace;
    }

    /**
     * Setter method for errorPlace.
     *
     * @param aErrorPlace the new value for errorPlace
     */
    public void setErrorPlace(String aErrorPlace) {
        errorPlace = aErrorPlace;
    }

    /**
     * Access method for errorDescription.
     *
     * @return the current value of errorDescription
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * Setter method for errorDescription.
     *
     * @param aErrorDescription the new value for errorDescription
     */
    public void setErrorDescription(String aErrorDescription) {
        errorDescription = aErrorDescription;
    }

    /**
     * Access method for errorGlobalIds.
     *
     * @return the current value of errorGlobalIds
     */
    public String getErrorGlobalIds() {
        return errorGlobalIds;
    }

    /**
     * Setter method for errorGlobalIds.
     *
     * @param aErrorGlobalIds the new value for errorGlobalIds
     */
    public void setErrorGlobalIds(String aErrorGlobalIds) {
        errorGlobalIds = aErrorGlobalIds;
    }

    /**
     * Access method for errorExceptionName.
     *
     * @return the current value of errorExceptionName
     */
    public String getErrorExceptionName() {
        return errorExceptionName;
    }

    /**
     * Setter method for errorExceptionName.
     *
     * @param aErrorExceptionName the new value for errorExceptionName
     */
    public void setErrorExceptionName(String aErrorExceptionName) {
        errorExceptionName = aErrorExceptionName;
    }

    /**
     * Access method for errorKey.
     *
     * @return the current value of errorKey
     */
    public String getErrorKey() {
        return errorKey;
    }

    /**
     * Setter method for errorKey.
     *
     * @param aErrorKey the new value for errorKey
     */
    public void setErrorKey(String aErrorKey) {
        errorKey = aErrorKey;
    }
  

    public Integer getExecutionId() {
		return executionId;
	}

	public void setExecutionId(Integer executionId) {
		this.executionId = executionId;
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

	/**
     * Returns a hash code for this instance.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        int i;
        int result = 17;
        if (getErrorKey() == null) {
            i = 0;
        } else {
            i = getErrorKey().hashCode();
        }
        result = 37*result + i;
        return result;
    }

    /**
     * Returns a debug-friendly String representation of this instance.
     *
     * @return String representation of this instance
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("[ConnectorExecutionErrors |");
        sb.append(" errorKey=").append(getErrorKey());
        sb.append("]");
        return sb.toString();
    }

    /**
     * Return all elements of the primary key.
     *
     * @return Map of key names to values
     */
    public Map<String, Object> getPrimaryKey() {
        Map<String, Object> ret = new LinkedHashMap<String, Object>(6);
        ret.put("errorKey", getErrorKey());
        return ret;
    }

}
