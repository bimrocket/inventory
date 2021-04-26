// Generated with g9.

package cat.santfeliu.api.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity(name="sources")
@IdClass(SourceModelId.class)
public class SourceModel implements Serializable {

    /** Primary key. */
    protected static final String PK = "SourceModelSourcesPk";

    @Id
    @Column(name="source_name", nullable=false, length=255)
    private String sourceName;
    @Column(name="source_type", nullable=false, length=255)
    private String sourceType;
    @Id
    @Column(name="source_param_name", nullable=false, length=255)
    private String sourceParamName;
    @Column(name="source_param_value", nullable=false, length=255)
    private String sourceParamValue;

    /** Default constructor. */
    public SourceModel() {
        super();
    }

    /**
     * Access method for sourceName.
     *
     * @return the current value of sourceName
     */
    public String getSourceName() {
        return sourceName;
    }

    /**
     * Setter method for sourceName.
     *
     * @param aSourceName the new value for sourceName
     */
    public void setSourceName(String aSourceName) {
        sourceName = aSourceName;
    }

    /**
     * Access method for sourceType.
     *
     * @return the current value of sourceType
     */
    public String getSourceType() {
        return sourceType;
    }

    /**
     * Setter method for sourceType.
     *
     * @param aSourceType the new value for sourceType
     */
    public void setSourceType(String aSourceType) {
        sourceType = aSourceType;
    }

    /**
     * Access method for sourceParamName.
     *
     * @return the current value of sourceParamName
     */
    public String getSourceParamName() {
        return sourceParamName;
    }

    /**
     * Setter method for sourceParamName.
     *
     * @param aSourceParamName the new value for sourceParamName
     */
    public void setSourceParamName(String aSourceParamName) {
        sourceParamName = aSourceParamName;
    }

    /**
     * Access method for sourceParamValue.
     *
     * @return the current value of sourceParamValue
     */
    public String getSourceParamValue() {
        return sourceParamValue;
    }

    /**
     * Setter method for sourceParamValue.
     *
     * @param aSourceParamValue the new value for sourceParamValue
     */
    public void setSourceParamValue(String aSourceParamValue) {
        sourceParamValue = aSourceParamValue;
    }

    /**
     * Compares the key for this instance with another SourceModel.
     *
     * @param other The object to compare to
     * @return True if other object is instance of class SourceModel and the key objects are equal
     */
    private boolean equalKeys(Object other) {
        if (this==other) {
            return true;
        }
        if (!(other instanceof SourceModel)) {
            return false;
        }
        SourceModel that = (SourceModel) other;
        Object mySourceName = this.getSourceName();
        Object yourSourceName = that.getSourceName();
        if (mySourceName==null ? yourSourceName!=null : !mySourceName.equals(yourSourceName)) {
            return false;
        }
        Object mySourceParamName = this.getSourceParamName();
        Object yourSourceParamName = that.getSourceParamName();
        if (mySourceParamName==null ? yourSourceParamName!=null : !mySourceParamName.equals(yourSourceParamName)) {
            return false;
        }
        return true;
    }

    /**
     * Compares this instance with another SourceModel.
     *
     * @param other The object to compare to
     * @return True if the objects are the same
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SourceModel)) return false;
        return this.equalKeys(other) && ((SourceModel)other).equalKeys(this);
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
        if (getSourceName() == null) {
            i = 0;
        } else {
            i = getSourceName().hashCode();
        }
        result = 37*result + i;
        if (getSourceParamName() == null) {
            i = 0;
        } else {
            i = getSourceParamName().hashCode();
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
        StringBuffer sb = new StringBuffer("[SourceModel |");
        sb.append(" sourceName=").append(getSourceName());
        sb.append(" sourceParamName=").append(getSourceParamName());
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
        ret.put("sourceName", getSourceName());
        ret.put("sourceParamName", getSourceParamName());
        return ret;
    }

}
