package cat.santfeliu.api.model;

import java.io.Serializable;

/**
 * IdClass for primary key when using JPA annotations
 */
public class SourceModelId implements Serializable {
    java.lang.String sourceName;
    java.lang.String sourceParamName;
    
    public SourceModelId() {
    	
    }

	public SourceModelId(String sourceName, String sourceParamName) {
		super();
		this.sourceName = sourceName;
		this.sourceParamName = sourceParamName;
	}

	public java.lang.String getSourceName() {
		return sourceName;
	}

	public void setSourceName(java.lang.String sourceName) {
		this.sourceName = sourceName;
	}

	public java.lang.String getSourceParamName() {
		return sourceParamName;
	}

	public void setSourceParamName(java.lang.String sourceParamName) {
		this.sourceParamName = sourceParamName;
	}
	
	
    
    
}
