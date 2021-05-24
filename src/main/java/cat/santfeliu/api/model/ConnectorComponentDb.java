// Generated with g9.

package cat.santfeliu.api.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="connector_component")
public class ConnectorComponentDb implements Serializable {

    /** Primary key. */
    protected static final String PK = "connectorComponentName";

    @Id
    @Column(name="connector_component_name", unique=true, nullable=false, length=100)
    private String connectorComponentName;    
    @Column(name="connector_component_type", length=100, nullable=false)
    private String connectorComponentType;
    @Column(name="connector_component_class", length=400)
    private String connectorComponentClass;

    /** Default constructor. */
    public ConnectorComponentDb() {
        super();
    }

    /**
     * Access method for connectorComponentName.
     *
     * @return the current value of connectorComponentName
     */
    public String getConnectorComponentName() {
        return connectorComponentName;
    }

    /**
     * Setter method for connectorComponentName.
     *
     * @param aConnectorComponentName the new value for connectorComponentName
     */
    public void setConnectorComponentName(String aConnectorComponentName) {
        connectorComponentName = aConnectorComponentName;
    }

    /**
     * Access method for connectorComponentClass.
     *
     * @return the current value of connectorComponentClass
     */
    public String getConnectorComponentClass() {
        return connectorComponentClass;
    }

    /**
     * Setter method for connectorComponentClass.
     *
     * @param aConnectorComponentClass the new value for connectorComponentClass
     */
    public void setConnectorComponentClass(String aConnectorComponentClass) {
        connectorComponentClass = aConnectorComponentClass;
    }

    
    
    public String getConnectorComponentType() {
		return connectorComponentType;
	}

	public void setConnectorComponentType(String connectorComponentType) {
		this.connectorComponentType = connectorComponentType;
	}

	/**
     * Compares the key for this instance with another ConnectorComponentDb.
     *
     * @param other The object to compare to
     * @return True if other object is instance of class ConnectorComponentDb and the key objects are equal
     */
    private boolean equalKeys(Object other) {
        if (this==other) {
            return true;
        }
        if (!(other instanceof ConnectorComponentDb)) {
            return false;
        }
        ConnectorComponentDb that = (ConnectorComponentDb) other;
        Object myConnectorComponentName = this.getConnectorComponentName();
        Object yourConnectorComponentName = that.getConnectorComponentName();
        if (myConnectorComponentName==null ? yourConnectorComponentName!=null : !myConnectorComponentName.equals(yourConnectorComponentName)) {
            return false;
        }
        return true;
    }

    /**
     * Compares this instance with another ConnectorComponentDb.
     *
     * @param other The object to compare to
     * @return True if the objects are the same
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ConnectorComponentDb)) return false;
        return this.equalKeys(other) && ((ConnectorComponentDb)other).equalKeys(this);
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
        if (getConnectorComponentName() == null) {
            i = 0;
        } else {
            i = getConnectorComponentName().hashCode();
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
        StringBuffer sb = new StringBuffer("[ConnectorComponentDb |");
        sb.append(" connectorComponentName=").append(getConnectorComponentName());
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
        ret.put("connectorComponentName", getConnectorComponentName());
        return ret;
    }

}
