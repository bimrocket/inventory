package cat.santfeliu.api.beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SentinelError {

    protected static final Integer MAX_GLOBAL_IDS = 50;
    protected static final ObjectMapper mapper = new ObjectMapper();

	private Integer executionId;
	private String errorKey;
	private String errorPlace;
	private String connectorName;
	private String errorGlobalIds = "";
    private Integer globalIdsOccurrences = 0;
    private Integer errorOccurrences = 0;
    private String errorDescription = "";
    private String errorException = null;
    
	public SentinelError() {
		
	}
	
	public SentinelError conName(String connectorName) {
		this.connectorName = connectorName;
		return this;
	}
	
	public SentinelError exception(Exception e) {
		StackTraceElement elem = e.getStackTrace()[0];
		this.errorException = new StringBuilder().append("Exception :: ").append(e.getClass().getName()).append(" with message '").append(e.getMessage()).append("'").append(" occurred at ").append(elem.getMethodName()).append("@").append(elem.getClassName()).append("(line:").append(elem.getLineNumber()).append(")").toString();
		return this;
	}
	
	public SentinelError foundErr() {
		this.errorOccurrences++;
		return this;
	}
	
	public SentinelError foundErr(String globalId) {
		if (this.globalIdsOccurrences < MAX_GLOBAL_IDS) {
			this.errorGlobalIds = this.errorGlobalIds + (this.errorGlobalIds.isEmpty() ? globalId : (", " + globalId));
			this.globalIdsOccurrences++;
		}
		this.errorOccurrences++;
		return this;
	}
	
	
	public SentinelError describe(String errorDescription) {
		this.errorDescription = errorDescription;
		return this;
	}
	
	public Integer getExecutionId() {
		return executionId;
	}

	public void setExecutionId(Integer executionId) {
		this.executionId = executionId;
	}
	
	public SentinelError executionId(Integer executionId) {
		this.executionId = executionId;
		return this;
	}

	public String getErrorKey() {
		return errorKey;
	}

	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}
	
	public SentinelError errorKey(String errorKey) {
		this.errorKey = errorKey;
		return this;
	}

	

	public String getErrorPlace() {
		return errorPlace;
	}

	public void setErrorPlace(String errorPlace) {
		this.errorPlace = errorPlace;
	}
	
	public SentinelError errorPlace(String errorPlace) {
		this.errorPlace = errorPlace;
		return this;
	}
	
	

	public String getErrorGlobalIds() {
		return errorGlobalIds;
	}

	public void setErrorGlobalIds(String errorGlobalIds) {
		this.errorGlobalIds = errorGlobalIds;
	}

	public Integer getGlobalIdsOccurrences() {
		return globalIdsOccurrences;
	}

	public void setGlobalIdsOccurrences(Integer globalIdsOccurrences) {
		this.globalIdsOccurrences = globalIdsOccurrences;
	}

	public Integer getErrorOccurrences() {
		return errorOccurrences;
	}

	public void setErrorOccurrences(Integer errorOccurrences) {
		this.errorOccurrences = errorOccurrences;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	
	

	public String getErrorException() {
		return errorException;
	}

	public void setErrorException(String errorException) {
		this.errorException = errorException;
	}
	
	

	public String getConnectorName() {
		return connectorName;
	}

	public void setConnectorName(String connectorName) {
		this.connectorName = connectorName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SentinelError stack = (SentinelError) o;
		if (executionId != stack.executionId)
			return false;
		if (errorKey != null ? !errorKey.equals(stack.errorKey) : stack.errorKey != null)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (executionId ^ (executionId >>> 32));
		result = 31 * result + (errorKey != null ? errorKey.hashCode() : 0);
		return result;
	}
	
	@Override
	public String toString() {
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "{}";
		} 
	}

}
