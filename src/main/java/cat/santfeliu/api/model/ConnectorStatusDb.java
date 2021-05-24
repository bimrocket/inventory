// Generated with g9.

package cat.santfeliu.api.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="connector_status")
public class ConnectorStatusDb implements Serializable {
    
	@Id
	@Column(name="connector_name", nullable=false, length=100)
    private String connectorName;
    @Column(name="connector_status", nullable=false, length=100)
    private String connectorStatus;
    @Column(name="connector_start_date")
    private Date connectorStartDate;
    @Column(name="connector_end_date")
    private Date connectorEndDate;

    /** Default constructor. */
    public ConnectorStatusDb() {
        super();
    }

    /**
     * Access method for connectorStatus.
     *
     * @return the current value of connectorStatus
     */
    public String getConnectorStatus() {
        return connectorStatus;
    }

    /**
     * Setter method for connectorStatus.
     *
     * @param aConnectorStatus the new value for connectorStatus
     */
    public void setConnectorStatus(String aConnectorStatus) {
        connectorStatus = aConnectorStatus;
    }

    /**
     * Access method for connectorStartDate.
     *
     * @return the current value of connectorStartDate
     */
    public Date getConnectorStartDate() {
        return connectorStartDate;
    }

    /**
     * Setter method for connectorStartDate.
     *
     * @param aConnectorStartDate the new value for connectorStartDate
     */
    public void setConnectorStartDate(Date aConnectorStartDate) {
        connectorStartDate = aConnectorStartDate;
    }

    /**
     * Access method for connectorEndDate.
     *
     * @return the current value of connectorEndDate
     */
    public Date getConnectorEndDate() {
        return connectorEndDate;
    }

    /**
     * Setter method for connectorEndDate.
     *
     * @param aConnectorEndDate the new value for connectorEndDate
     */
    public void setConnectorEndDate(Date aConnectorEndDate) {
        connectorEndDate = aConnectorEndDate;
    }

	public String getConnectorName() {
		return connectorName;
	}

	public void setConnectorName(String connectorName) {
		this.connectorName = connectorName;
	}
    
    

}
