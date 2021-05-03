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

@Entity(name="input_output_io")
public class InputOutputIoModel implements Serializable {

    /** Primary key. */
    protected static final String PK = "ioId";

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="io_id", unique=true, nullable=false, precision=10)
    private int ioId;
    @Column(name="io_description")
    private String ioDescription;
    @Column(name="output_data_model", nullable=false, length=255)
    private String outputDataModel;
    @Column(name="output_data_format", nullable=false, length=255)
    private String outputDataFormat;
    @Column(name="output_template_or_script")
    private String outputTemplateOrScript;
    @Column(name="output_interpreter_id", nullable=false, precision=10)
    private int outputInterpreterId;
    @Column(name="input_data_format", nullable=false, length=255)
    private String inputDataFormat;
    @Column(name="input_data_path", nullable=false)
    private String inputDataPath;
    @Column(name="input_local_id_path", nullable=false, length=255)
    private String inputLocalIdPath;
    @Column(name="input_local_model_id_path", nullable=false, length=255)
    private String inputLocalModelIdPath;
    @Column(name="input_interpreter_id", nullable=false, precision=10)
    private int inputInterpreterId;
    @Column(name="input_local_model_name", nullable=false, length=255)
    private String inputLocalModelName;

    /** Default constructor. */
    public InputOutputIoModel() {
        super();
    }

    /**
     * Access method for ioId.
     *
     * @return the current value of ioId
     */
    public int getIoId() {
        return ioId;
    }

    /**
     * Setter method for ioId.
     *
     * @param aIoId the new value for ioId
     */
    public void setIoId(int aIoId) {
        ioId = aIoId;
    }

    /**
     * Access method for ioDescription.
     *
     * @return the current value of ioDescription
     */
    public String getIoDescription() {
        return ioDescription;
    }

    /**
     * Setter method for ioDescription.
     *
     * @param aIoDescription the new value for ioDescription
     */
    public void setIoDescription(String aIoDescription) {
        ioDescription = aIoDescription;
    }

    /**
     * Access method for outputDataModel.
     *
     * @return the current value of outputDataModel
     */
    public String getOutputDataModel() {
        return outputDataModel;
    }

    /**
     * Setter method for outputDataModel.
     *
     * @param aOutputDataModel the new value for outputDataModel
     */
    public void setOutputDataModel(String aOutputDataModel) {
        outputDataModel = aOutputDataModel;
    }

    /**
     * Access method for outputDataFormat.
     *
     * @return the current value of outputDataFormat
     */
    public String getOutputDataFormat() {
        return outputDataFormat;
    }

    /**
     * Setter method for outputDataFormat.
     *
     * @param aOutputDataFormat the new value for outputDataFormat
     */
    public void setOutputDataFormat(String aOutputDataFormat) {
        outputDataFormat = aOutputDataFormat;
    }

    /**
     * Access method for outputTemplateOrScript.
     *
     * @return the current value of outputTemplateOrScript
     */
    public String getOutputTemplateOrScript() {
        return outputTemplateOrScript;
    }

    /**
     * Setter method for outputTemplateOrScript.
     *
     * @param aOutputTemplateOrScript the new value for outputTemplateOrScript
     */
    public void setOutputTemplateOrScript(String aOutputTemplateOrScript) {
        outputTemplateOrScript = aOutputTemplateOrScript;
    }

    /**
     * Access method for outputInterpreterId.
     *
     * @return the current value of outputInterpreterId
     */
    public int getOutputInterpreterId() {
        return outputInterpreterId;
    }

    /**
     * Setter method for outputInterpreterId.
     *
     * @param aOutputInterpreterId the new value for outputInterpreterId
     */
    public void setOutputInterpreterId(int aOutputInterpreterId) {
        outputInterpreterId = aOutputInterpreterId;
    }

    /**
     * Access method for inputDataFormat.
     *
     * @return the current value of inputDataFormat
     */
    public String getInputDataFormat() {
        return inputDataFormat;
    }

    /**
     * Setter method for inputDataFormat.
     *
     * @param aInputDataFormat the new value for inputDataFormat
     */
    public void setInputDataFormat(String aInputDataFormat) {
        inputDataFormat = aInputDataFormat;
    }

    /**
     * Access method for inputDataPath.
     *
     * @return the current value of inputDataPath
     */
    public String getInputDataPath() {
        return inputDataPath;
    }

    /**
     * Setter method for inputDataPath.
     *
     * @param aInputDataPath the new value for inputDataPath
     */
    public void setInputDataPath(String aInputDataPath) {
        inputDataPath = aInputDataPath;
    }

    /**
     * Access method for inputLocalIdPath.
     *
     * @return the current value of inputLocalIdPath
     */
    public String getInputLocalIdPath() {
        return inputLocalIdPath;
    }

    /**
     * Setter method for inputLocalIdPath.
     *
     * @param aInputLocalIdPath the new value for inputLocalIdPath
     */
    public void setInputLocalIdPath(String aInputLocalIdPath) {
        inputLocalIdPath = aInputLocalIdPath;
    }

    /**
     * Access method for inputLocalModelIdPath.
     *
     * @return the current value of inputLocalModelIdPath
     */
    public String getInputLocalModelIdPath() {
        return inputLocalModelIdPath;
    }

    /**
     * Setter method for inputLocalModelIdPath.
     *
     * @param aInputLocalModelIdPath the new value for inputLocalModelIdPath
     */
    public void setInputLocalModelIdPath(String aInputLocalModelIdPath) {
        inputLocalModelIdPath = aInputLocalModelIdPath;
    }

    /**
     * Access method for inputInterpreterId.
     *
     * @return the current value of inputInterpreterId
     */
    public int getInputInterpreterId() {
        return inputInterpreterId;
    }

    /**
     * Setter method for inputInterpreterId.
     *
     * @param aInputInterpreterId the new value for inputInterpreterId
     */
    public void setInputInterpreterId(int aInputInterpreterId) {
        inputInterpreterId = aInputInterpreterId;
    }
    
    

    public String getInputLocalModelName() {
		return inputLocalModelName;
	}

	public void setInputLocalModelName(String inputLocalModelName) {
		this.inputLocalModelName = inputLocalModelName;
	}

	/**
     * Compares the key for this instance with another InputOutputIoModel.
     *
     * @param other The object to compare to
     * @return True if other object is instance of class InputOutputIoModel and the key objects are equal
     */
    private boolean equalKeys(Object other) {
        if (this==other) {
            return true;
        }
        if (!(other instanceof InputOutputIoModel)) {
            return false;
        }
        InputOutputIoModel that = (InputOutputIoModel) other;
        if (this.getIoId() != that.getIoId()) {
            return false;
        }
        return true;
    }

    /**
     * Compares this instance with another InputOutputIoModel.
     *
     * @param other The object to compare to
     * @return True if the objects are the same
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof InputOutputIoModel)) return false;
        return this.equalKeys(other) && ((InputOutputIoModel)other).equalKeys(this);
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
        i = getIoId();
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
        StringBuffer sb = new StringBuffer("[InputOutputIoModel |");
        sb.append(" ioId=").append(getIoId());
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
        ret.put("ioId", Integer.valueOf(getIoId()));
        return ret;
    }

}
