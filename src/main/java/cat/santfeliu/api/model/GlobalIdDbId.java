package cat.santfeliu.api.model;

import java.io.Serializable;

/**
 * IdClass for primary key when using JPA annotations
 */
public class GlobalIdDbId implements Serializable {
    java.lang.String localId;
    java.lang.String globalId;
    
    
	public GlobalIdDbId(String localId, String globalId) {
		super();
		this.localId = localId;
		this.globalId = globalId;
	}
	
	public java.lang.String getLocalId() {
		return localId;
	}
	public void setLocalId(java.lang.String localId) {
		this.localId = localId;
	}
	public java.lang.String getGlobalId() {
		return globalId;
	}
	public void setGlobalId(java.lang.String globalId) {
		this.globalId = globalId;
	}
    
    
}
