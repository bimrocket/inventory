package cat.santfeliu.api.model;

import java.io.Serializable;

public class GlobalIdModelId implements Serializable {

    java.lang.String localId;
    java.lang.String globalId;
    
    
	public GlobalIdModelId() {
	}
	
	public GlobalIdModelId(String localId, String globalId) {
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
