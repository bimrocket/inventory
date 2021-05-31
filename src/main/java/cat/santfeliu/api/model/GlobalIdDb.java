// Generated with g9.

package cat.santfeliu.api.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity(name="global_id")
@IdClass(GlobalIdDbId.class)
public class GlobalIdDb implements Serializable {


    /** Primary key. */
    protected static final String PK = "GlobalIdDbPkGlobalId";
    
    @Id
    @Column(name="inventory_name", nullable=false, length=100)
    private String inventoryName;
    @Id
    @Column(name="local_id", nullable=false, length=255)
    private String localId;
    @Id
    @Column(name="global_id", nullable=false, length=255)
    private String globalId;

    /** Default constructor. */
    public GlobalIdDb() {
        super();
    }
    
    

    public String getInventoryName() {
		return inventoryName;
	}



	public void setInventoryName(String inventoryName) {
		this.inventoryName = inventoryName;
	}



	/**
     * Access method for localId.
     *
     * @return the current value of localId
     */
    public String getLocalId() {
        return localId;
    }

    /**
     * Setter method for localId.
     *
     * @param aLocalId the new value for localId
     */
    public void setLocalId(String aLocalId) {
        localId = aLocalId;
    }

    /**
     * Access method for globalId.
     *
     * @return the current value of globalId
     */
    public String getGlobalId() {
        return globalId;
    }

    /**
     * Setter method for globalId.
     *
     * @param aGlobalId the new value for globalId
     */
    public void setGlobalId(String aGlobalId) {
        globalId = aGlobalId;
    }

    /**
     * Compares the key for this instance with another GlobalIdDb.
     *
     * @param other The object to compare to
     * @return True if other object is instance of class GlobalIdDb and the key objects are equal
     */
    private boolean equalKeys(Object other) {
        if (this==other) {
            return true;
        }
        if (!(other instanceof GlobalIdDb)) {
            return false;
        }
        GlobalIdDb that = (GlobalIdDb) other;
        Object myLocalId = this.getLocalId();
        Object yourLocalId = that.getLocalId();
        if (myLocalId==null ? yourLocalId!=null : !myLocalId.equals(yourLocalId)) {
            return false;
        }
        Object myGlobalId = this.getGlobalId();
        Object yourGlobalId = that.getGlobalId();
        if (myGlobalId==null ? yourGlobalId!=null : !myGlobalId.equals(yourGlobalId)) {
            return false;
        }
        return true;
    }

    /**
     * Compares this instance with another GlobalIdDb.
     *
     * @param other The object to compare to
     * @return True if the objects are the same
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof GlobalIdDb)) return false;
        return this.equalKeys(other) && ((GlobalIdDb)other).equalKeys(this);
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
        if (getLocalId() == null) {
            i = 0;
        } else {
            i = getLocalId().hashCode();
        }
        result = 37*result + i;
        if (getGlobalId() == null) {
            i = 0;
        } else {
            i = getGlobalId().hashCode();
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
        StringBuffer sb = new StringBuffer("[GlobalIdDb |");
        sb.append(" localId=").append(getLocalId());
        sb.append(" globalId=").append(getGlobalId());
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
        ret.put("localId", getLocalId());
        ret.put("globalId", getGlobalId());
        return ret;
    }

}
