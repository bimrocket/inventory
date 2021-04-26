// Generated with g9.

package cat.santfeliu.api.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public final class SourcesPk {

    @Column(name="source_name", nullable=false, length=255)
    private final String sourceName;
    @Column(name="source_param_name", nullable=false, length=255)
    private final String sourceParamName;

    /** Initializing constructor. */
    public SourcesPk(String sourceName, String sourceParamName) {
        this.sourceName = sourceName;
        this.sourceParamName = sourceParamName;
    }

    /** Private default constructor (for ORM frameworks). */
    @SuppressWarnings("unused")
    private SourcesPk() {
        this(null, null);
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
     * Access method for sourceParamName.
     *
     * @return the current value of sourceParamName
     */
    public String getSourceParamName() {
        return sourceParamName;
    }

    /**
     * Compares this instance with another SourcesPk.
     *
     * @param other The object to compare to
     * @return True if the objects are the same
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return false;
        }
        SourcesPk that = (SourcesPk) other;
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
     * Returns a hash code for this instance.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        int i = 1;
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

}
