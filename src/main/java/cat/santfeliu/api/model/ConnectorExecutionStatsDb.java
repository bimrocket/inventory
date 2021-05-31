// Generated with g9.

package cat.santfeliu.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity(name="connector_execution_stats")
public class ConnectorExecutionStatsDb implements Serializable {

    /** Primary key. */
    protected static final String PK = "executionId";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "connector_execution_seq")
    @SequenceGenerator(name = "connector_execution_seq", sequenceName = "connector_execution_seq", allocationSize = 1)
    @Column(name="execution_id", unique=true, nullable=false, precision=10)
    private int executionId;
    @Column(name="execution_start_date", nullable=false)
    private Date executionStartDate;
    @Column(name="execution_end_date")
    private Date executionEndDate;
    @Column(name="execution_objects_loaded", precision=10)
    private int executionObjectsLoaded;
    @Column(name="execution_objects_transformed", precision=10)
    private int executionObjectsTransformed;
    @Column(name="execution_objects_sent", precision=10)
    private int executionObjectsSent;
    @Column(name="execution_final_state", length=100)
    private String executionFinalState;
    @Column(name="execution_connector_name", length=100)
    private String executionConnectorName;

    /** Default constructor. */
    public ConnectorExecutionStatsDb() {
        super();
    }

    /**
     * Access method for executionId.
     *
     * @return the current value of executionId
     */
    public int getExecutionId() {
        return executionId;
    }

    /**
     * Setter method for executionId.
     *
     * @param aExecutionId the new value for executionId
     */
    public void setExecutionId(int aExecutionId) {
        executionId = aExecutionId;
    }

    /**
     * Access method for executionStartDate.
     *
     * @return the current value of executionStartDate
     */
    public Date getExecutionStartDate() {
        return executionStartDate;
    }

    /**
     * Setter method for executionStartDate.
     *
     * @param aExecutionStartDate the new value for executionStartDate
     */
    public void setExecutionStartDate(Date aExecutionStartDate) {
        executionStartDate = aExecutionStartDate;
    }

    /**
     * Access method for executionEndDate.
     *
     * @return the current value of executionEndDate
     */
    public Date getExecutionEndDate() {
        return executionEndDate;
    }

    /**
     * Setter method for executionEndDate.
     *
     * @param aExecutionEndDate the new value for executionEndDate
     */
    public void setExecutionEndDate(Date aExecutionEndDate) {
        executionEndDate = aExecutionEndDate;
    }

    /**
     * Access method for executionObjectsLoaded.
     *
     * @return the current value of executionObjectsLoaded
     */
    public int getExecutionObjectsLoaded() {
        return executionObjectsLoaded;
    }

    /**
     * Setter method for executionObjectsLoaded.
     *
     * @param aExecutionObjectsLoaded the new value for executionObjectsLoaded
     */
    public void setExecutionObjectsLoaded(int aExecutionObjectsLoaded) {
        executionObjectsLoaded = aExecutionObjectsLoaded;
    }
    
    public void addLoaded() {
    	executionObjectsLoaded = executionObjectsLoaded + 1;
    }

    /**
     * Access method for executionObjectsTransformed.
     *
     * @return the current value of executionObjectsTransformed
     */
    public int getExecutionObjectsTransformed() {
        return executionObjectsTransformed;
    }

    /**
     * Setter method for executionObjectsTransformed.
     *
     * @param aExecutionObjectsTransformed the new value for executionObjectsTransformed
     */
    public void setExecutionObjectsTransformed(int aExecutionObjectsTransformed) {
        executionObjectsTransformed = aExecutionObjectsTransformed;
    }
    
    public void addTransformed() {
    	executionObjectsTransformed = executionObjectsTransformed + 1;
    }

    /**
     * Access method for executionObjectsSent.
     *
     * @return the current value of executionObjectsSent
     */
    public int getExecutionObjectsSent() {
        return executionObjectsSent;
    }

    /**
     * Setter method for executionObjectsSent.
     *
     * @param aExecutionObjectsSent the new value for executionObjectsSent
     */
    public void setExecutionObjectsSent(int aExecutionObjectsSent) {
        executionObjectsSent = aExecutionObjectsSent;
    }
    
    public void addSent() {
    	executionObjectsSent = executionObjectsSent + 1;
    }

    /**
     * Access method for executionFinalState.
     *
     * @return the current value of executionFinalState
     */
    public String getExecutionFinalState() {
        return executionFinalState;
    }

    /**
     * Setter method for executionFinalState.
     *
     * @param aExecutionFinalState the new value for executionFinalState
     */
    public void setExecutionFinalState(String aExecutionFinalState) {
        executionFinalState = aExecutionFinalState;
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
        i = getExecutionId();
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
        StringBuffer sb = new StringBuffer("[ConnectorExecutionStats |");
        sb.append(" executionId=").append(getExecutionId());
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
        ret.put("executionId", Integer.valueOf(getExecutionId()));
        return ret;
    }

}
