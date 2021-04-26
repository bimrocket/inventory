// Generated with g9.

package cat.santfeliu.api.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="interpreters")
public class InterpreterModel implements Serializable {

    /** Primary key. */
    protected static final String PK = "interpreterId";

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="interpreter_id", unique=true, nullable=false, precision=10)
    private int interpreterId;
    @Column(name="interpreter_language", nullable=false, length=255)
    private String interpreterLanguage;
    @Column(name="interpreter_name", nullable=false, length=255)
    private String interpreterName;
    @Column(name="interpreter_input", nullable=false, length=1)
    private boolean interpreterInput;
    @Column(name="interpreter_output", nullable=false, length=1)
    private boolean interpreterOutput;
    @Column(name="interpreter_scriptable", nullable=false, length=1)
    private boolean interpreterScriptable;

    /** Default constructor. */
    public InterpreterModel() {
        super();
    }

    /**
     * Access method for interpreterId.
     *
     * @return the current value of interpreterId
     */
    public int getInterpreterId() {
        return interpreterId;
    }

    /**
     * Setter method for interpreterId.
     *
     * @param aInterpreterId the new value for interpreterId
     */
    public void setInterpreterId(int aInterpreterId) {
        interpreterId = aInterpreterId;
    }

    /**
     * Access method for interpreterLanguage.
     *
     * @return the current value of interpreterLanguage
     */
    public String getInterpreterLanguage() {
        return interpreterLanguage;
    }

    /**
     * Setter method for interpreterLanguage.
     *
     * @param aInterpreterLanguage the new value for interpreterLanguage
     */
    public void setInterpreterLanguage(String aInterpreterLanguage) {
        interpreterLanguage = aInterpreterLanguage;
    }

    /**
     * Access method for interpreterName.
     *
     * @return the current value of interpreterName
     */
    public String getInterpreterName() {
        return interpreterName;
    }

    /**
     * Setter method for interpreterName.
     *
     * @param aInterpreterName the new value for interpreterName
     */
    public void setInterpreterName(String aInterpreterName) {
        interpreterName = aInterpreterName;
    }

    /**
     * Access method for interpreterInput.
     *
     * @return true if and only if interpreterInput is currently true
     */
    public boolean getInterpreterInput() {
        return interpreterInput;
    }

    /**
     * Setter method for interpreterInput.
     *
     * @param aInterpreterInput the new value for interpreterInput
     */
    public void setInterpreterInput(boolean aInterpreterInput) {
        interpreterInput = aInterpreterInput;
    }

    /**
     * Access method for interpreterOutput.
     *
     * @return true if and only if interpreterOutput is currently true
     */
    public boolean getInterpreterOutput() {
        return interpreterOutput;
    }

    /**
     * Setter method for interpreterOutput.
     *
     * @param aInterpreterOutput the new value for interpreterOutput
     */
    public void setInterpreterOutput(boolean aInterpreterOutput) {
        interpreterOutput = aInterpreterOutput;
    }

    /**
     * Access method for interpreterScriptable.
     *
     * @return true if and only if interpreterScriptable is currently true
     */
    public boolean getInterpreterScriptable() {
        return interpreterScriptable;
    }

    /**
     * Setter method for interpreterScriptable.
     *
     * @param aInterpreterScriptable the new value for interpreterScriptable
     */
    public void setInterpreterScriptable(boolean aInterpreterScriptable) {
        interpreterScriptable = aInterpreterScriptable;
    }

    /**
     * Compares the key for this instance with another InterpreterModel.
     *
     * @param other The object to compare to
     * @return True if other object is instance of class InterpreterModel and the key objects are equal
     */
    private boolean equalKeys(Object other) {
        if (this==other) {
            return true;
        }
        if (!(other instanceof InterpreterModel)) {
            return false;
        }
        InterpreterModel that = (InterpreterModel) other;
        if (this.getInterpreterId() != that.getInterpreterId()) {
            return false;
        }
        return true;
    }

    /**
     * Compares this instance with another InterpreterModel.
     *
     * @param other The object to compare to
     * @return True if the objects are the same
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof InterpreterModel)) return false;
        return this.equalKeys(other) && ((InterpreterModel)other).equalKeys(this);
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
        i = getInterpreterId();
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
        StringBuffer sb = new StringBuffer("[InterpreterModel |");
        sb.append(" interpreterId=").append(getInterpreterId());
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
        ret.put("interpreterId", Integer.valueOf(getInterpreterId()));
        return ret;
    }

}
