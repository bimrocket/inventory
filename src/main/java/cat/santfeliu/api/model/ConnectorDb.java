// Generated with g9.

package cat.santfeliu.api.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="connector", indexes={@Index(name="connector_inventory_name_IX", columnList="inventory_name", unique=true)})
public class ConnectorDb implements Serializable {

    /** Primary key. */
    protected static final String PK = "connectorName";

    @Id
    @Column(name="connector_name", unique=true, nullable=false, length=255)
    private String connectorName;
    @Column(name="connector_loader", nullable=false, length=400)
    private String connectorLoader;
    @Column(name="connector_transformer", nullable=false, length=400)
    private String connectorTransformer;
    @Column(name="connector_sender", nullable=false, length=400)
    private String connectorSender;
    @Column(name="inventory_name", unique=true, nullable=false, length=255)
    private String inventoryName;

    /** Default constructor. */
    public ConnectorDb() {
        super();
    }

    /**
     * Access method for connectorName.
     *
     * @return the current value of connectorName
     */
    public String getConnectorName() {
        return connectorName;
    }

    /**
     * Setter method for connectorName.
     *
     * @param aConnectorName the new value for connectorName
     */
    public void setConnectorName(String aConnectorName) {
        connectorName = aConnectorName;
    }

    /**
     * Access method for connectorLoader.
     *
     * @return the current value of connectorLoader
     */
    public String getConnectorLoader() {
        return connectorLoader;
    }

    /**
     * Setter method for connectorLoader.
     *
     * @param aConnectorLoader the new value for connectorLoader
     */
    public void setConnectorLoader(String aConnectorLoader) {
        connectorLoader = aConnectorLoader;
    }

    /**
     * Access method for connectorTransformer.
     *
     * @return the current value of connectorTransformer
     */
    public String getConnectorTransformer() {
        return connectorTransformer;
    }

    /**
     * Setter method for connectorTransformer.
     *
     * @param aConnectorTransformer the new value for connectorTransformer
     */
    public void setConnectorTransformer(String aConnectorTransformer) {
        connectorTransformer = aConnectorTransformer;
    }

    /**
     * Access method for connectorSender.
     *
     * @return the current value of connectorSender
     */
    public String getConnectorSender() {
        return connectorSender;
    }

    /**
     * Setter method for connectorSender.
     *
     * @param aConnectorSender the new value for connectorSender
     */
    public void setConnectorSender(String aConnectorSender) {
        connectorSender = aConnectorSender;
    }

    /**
     * Access method for inventoryName.
     *
     * @return the current value of inventoryName
     */
    public String getInventoryName() {
        return inventoryName;
    }

    /**
     * Setter method for inventoryName.
     *
     * @param aInventoryName the new value for inventoryName
     */
    public void setInventoryName(String aInventoryName) {
        inventoryName = aInventoryName;
    }

    /**
     * Compares the key for this instance with another ConnectorDb.
     *
     * @param other The object to compare to
     * @return True if other object is instance of class ConnectorDb and the key objects are equal
     */
    private boolean equalKeys(Object other) {
        if (this==other) {
            return true;
        }
        if (!(other instanceof ConnectorDb)) {
            return false;
        }
        ConnectorDb that = (ConnectorDb) other;
        Object myConnectorName = this.getConnectorName();
        Object yourConnectorName = that.getConnectorName();
        if (myConnectorName==null ? yourConnectorName!=null : !myConnectorName.equals(yourConnectorName)) {
            return false;
        }
        return true;
    }

    /**
     * Compares this instance with another ConnectorDb.
     *
     * @param other The object to compare to
     * @return True if the objects are the same
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ConnectorDb)) return false;
        return this.equalKeys(other) && ((ConnectorDb)other).equalKeys(this);
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
        if (getConnectorName() == null) {
            i = 0;
        } else {
            i = getConnectorName().hashCode();
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
        StringBuffer sb = new StringBuffer("[ConnectorDb |");
        sb.append(" connectorName=").append(getConnectorName());
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
        ret.put("connectorName", getConnectorName());
        return ret;
    }

}
