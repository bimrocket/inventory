// Generated with g9.

package cat.santfeliu.api.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="connector_component_config")
@IdClass(ConnectorComponentConfigDbId.class)
public class ConnectorComponentConfigDb implements Serializable {

    /** Primary key. */
    protected static final String PK = "ConnectorComponentConfigDbPkConnectorComponentConfig";

    @Id
    @Column(name="config_key", nullable=false, length=200)
    private String configKey;
    @Column(name="config_value", nullable=false)
    private String configValue;
    @Id
    @Column(name="connector_name", nullable=false, length=200)
    private String connectorName;
    @Id
    @Column(name="component_type", nullable=false, length=200)
    private String componentType;

    /** Default constructor. */
    public ConnectorComponentConfigDb() {
        super();
    }
    
    

    public String getConnectorName() {
		return connectorName;
	}



	public void setConnectorName(String connectorName) {
		this.connectorName = connectorName;
	}



	public String getComponentType() {
		return componentType;
	}



	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}



	/**
     * Access method for configKey.
     *
     * @return the current value of configKey
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * Setter method for configKey.
     *
     * @param aConfigKey the new value for configKey
     */
    public void setConfigKey(String aConfigKey) {
        configKey = aConfigKey;
    }

    /**
     * Access method for configValue.
     *
     * @return the current value of configValue
     */
    public String getConfigValue() {
        return configValue;
    }

    /**
     * Setter method for configValue.
     *
     * @param aConfigValue the new value for configValue
     */
    public void setConfigValue(String aConfigValue) {
        configValue = aConfigValue;
    }


    /**
     * Compares the key for this instance with another ConnectorComponentConfigDb.
     *
     * @param other The object to compare to
     * @return True if other object is instance of class ConnectorComponentConfigDb and the key objects are equal
     */
    private boolean equalKeys(Object other) {
        if (this==other) {
            return true;
        }
        if (!(other instanceof ConnectorComponentConfigDb)) {
            return false;
        }
        ConnectorComponentConfigDb that = (ConnectorComponentConfigDb) other;
        Object myConfigKey = this.getConfigKey();
        Object yourConfigKey = that.getConfigKey();
        if (myConfigKey==null ? yourConfigKey!=null : !myConfigKey.equals(yourConfigKey)) {
            return false;
        }
        return true;
    }

    /**
     * Compares this instance with another ConnectorComponentConfigDb.
     *
     * @param other The object to compare to
     * @return True if the objects are the same
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ConnectorComponentConfigDb)) return false;
        return this.equalKeys(other) && ((ConnectorComponentConfigDb)other).equalKeys(this);
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
        if (getConfigKey() == null) {
            i = 0;
        } else {
            i = getConfigKey().hashCode();
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
        StringBuffer sb = new StringBuffer("[ConnectorComponentConfigDb |");
        sb.append(" configKey=").append(getConfigKey());
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
        ret.put("configKey", getConfigKey());
        return ret;
    }

}
