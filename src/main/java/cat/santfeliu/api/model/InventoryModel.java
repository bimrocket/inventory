// Generated with g9.

package cat.santfeliu.api.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="inventories", indexes={@Index(name="inventories_inventory_job_api_id_IX", columnList="inventory_job_api_id", unique=true)})
public class InventoryModel implements Serializable {

    /** Primary key. */
    protected static final String PK = "inventoryId";

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="inventory_id", unique=true, nullable=false, precision=10)
    private int inventoryId;
    @Column(name="inventory_name", nullable=false, length=255)
    private String inventoryName;
    @Column(name="inventory_io_id", nullable=false, precision=10)
    private int inventoryIoId;
    @Column(name="inventory_job_api_id", unique=true, nullable=false, length=255)
    private String inventoryJobApiId;
    @Column(name="inventory_kafka_topic_id", nullable=false, length=255)
    private String inventoryKafkaTopicId;
    @Column(name="inventory_source_id", nullable=false, length=255)
    private String inventorySourceId;
    @OneToMany(mappedBy="inventoryModel")
    private Set<GlobalIdModel> globalIdModel;

    /** Default constructor. */
    public InventoryModel() {
        super();
    }

    /**
     * Access method for inventoryId.
     *
     * @return the current value of inventoryId
     */
    public int getInventoryId() {
        return inventoryId;
    }

    /**
     * Setter method for inventoryId.
     *
     * @param aInventoryId the new value for inventoryId
     */
    public void setInventoryId(int aInventoryId) {
        inventoryId = aInventoryId;
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
     * Access method for inventoryIoId.
     *
     * @return the current value of inventoryIoId
     */
    public int getInventoryIoId() {
        return inventoryIoId;
    }

    /**
     * Setter method for inventoryIoId.
     *
     * @param aInventoryIoId the new value for inventoryIoId
     */
    public void setInventoryIoId(int aInventoryIoId) {
        inventoryIoId = aInventoryIoId;
    }

    /**
     * Access method for inventoryJobApiId.
     *
     * @return the current value of inventoryJobApiId
     */
    public String getInventoryJobApiId() {
        return inventoryJobApiId;
    }

    /**
     * Setter method for inventoryJobApiId.
     *
     * @param aInventoryJobApiId the new value for inventoryJobApiId
     */
    public void setInventoryJobApiId(String aInventoryJobApiId) {
        inventoryJobApiId = aInventoryJobApiId;
    }

    /**
     * Access method for inventoryKafkaTopicId.
     *
     * @return the current value of inventoryKafkaTopicId
     */
    public String getInventoryKafkaTopicId() {
        return inventoryKafkaTopicId;
    }

    /**
     * Setter method for inventoryKafkaTopicId.
     *
     * @param aInventoryKafkaTopicId the new value for inventoryKafkaTopicId
     */
    public void setInventoryKafkaTopicId(String aInventoryKafkaTopicId) {
        inventoryKafkaTopicId = aInventoryKafkaTopicId;
    }

    /**
     * Access method for inventorySourceId.
     *
     * @return the current value of inventorySourceId
     */
    public String getInventorySourceId() {
        return inventorySourceId;
    }

    /**
     * Setter method for inventorySourceId.
     *
     * @param aInventorySourceId the new value for inventorySourceId
     */
    public void setInventorySourceId(String aInventorySourceId) {
        inventorySourceId = aInventorySourceId;
    }

    /**
     * Access method for globalIdModel.
     *
     * @return the current value of globalIdModel
     */
    public Set<GlobalIdModel> getGlobalIdModel() {
        return globalIdModel;
    }

    /**
     * Setter method for globalIdModel.
     *
     * @param aGlobalIdModel the new value for globalIdModel
     */
    public void setGlobalIdModel(Set<GlobalIdModel> aGlobalIdModel) {
        globalIdModel = aGlobalIdModel;
    }

    /**
     * Compares the key for this instance with another InventoryModel.
     *
     * @param other The object to compare to
     * @return True if other object is instance of class InventoryModel and the key objects are equal
     */
    private boolean equalKeys(Object other) {
        if (this==other) {
            return true;
        }
        if (!(other instanceof InventoryModel)) {
            return false;
        }
        InventoryModel that = (InventoryModel) other;
        if (this.getInventoryId() != that.getInventoryId()) {
            return false;
        }
        return true;
    }

    /**
     * Compares this instance with another InventoryModel.
     *
     * @param other The object to compare to
     * @return True if the objects are the same
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof InventoryModel)) return false;
        return this.equalKeys(other) && ((InventoryModel)other).equalKeys(this);
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
        i = getInventoryId();
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
        StringBuffer sb = new StringBuffer("[InventoryModel |");
        sb.append(" inventoryId=").append(getInventoryId());
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
        ret.put("inventoryId", Integer.valueOf(getInventoryId()));
        return ret;
    }

}
