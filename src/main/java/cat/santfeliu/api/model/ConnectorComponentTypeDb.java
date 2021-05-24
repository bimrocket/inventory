// Generated with g9.

package cat.santfeliu.api.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity(name="connector_component_type")
public class ConnectorComponentTypeDb implements Serializable {

    /** Primary key. */
    protected static final String PK = "componentType";

    @Id
    @Column(name="component_type", unique=true, nullable=false, length=100)
    private String componentType;

    /** Default constructor. */
    public ConnectorComponentTypeDb() {
        super();
    }

    /**
     * Access method for componentType.
     *
     * @return the current value of componentType
     */
    public String getComponentType() {
        return componentType;
    }

    /**
     * Setter method for componentType.
     *
     * @param aComponentType the new value for componentType
     */
    public void setComponentType(String aComponentType) {
        componentType = aComponentType;
    }
    
    /**
     * Compares the key for this instance with another ConnectorComponentTypeDb.
     *
     * @param other The object to compare to
     * @return True if other object is instance of class ConnectorComponentTypeDb and the key objects are equal
     */
    private boolean equalKeys(Object other) {
        if (this==other) {
            return true;
        }
        if (!(other instanceof ConnectorComponentTypeDb)) {
            return false;
        }
        ConnectorComponentTypeDb that = (ConnectorComponentTypeDb) other;
        Object myComponentType = this.getComponentType();
        Object yourComponentType = that.getComponentType();
        if (myComponentType==null ? yourComponentType!=null : !myComponentType.equals(yourComponentType)) {
            return false;
        }
        return true;
    }

    /**
     * Compares this instance with another ConnectorComponentTypeDb.
     *
     * @param other The object to compare to
     * @return True if the objects are the same
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ConnectorComponentTypeDb)) return false;
        return this.equalKeys(other) && ((ConnectorComponentTypeDb)other).equalKeys(this);
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
        if (getComponentType() == null) {
            i = 0;
        } else {
            i = getComponentType().hashCode();
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
        StringBuffer sb = new StringBuffer("[ConnectorComponentTypeDb |");
        sb.append(" componentType=").append(getComponentType());
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
        ret.put("componentType", getComponentType());
        return ret;
    }

}
