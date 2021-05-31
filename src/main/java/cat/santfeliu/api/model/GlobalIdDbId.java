package cat.santfeliu.api.model;

import java.io.Serializable;

/**
 * IdClass for primary key when using JPA annotations
 */
public class GlobalIdDbId implements Serializable {
    java.lang.String localId;
    java.lang.String globalId;
    java.lang.String inventoryName;
    
    public GlobalIdDbId() {
    	
    }
    
	public GlobalIdDbId(String localId, String globalId, String inventoryName) {
		super();
		this.localId = localId;
		this.globalId = globalId;
		this.inventoryName = inventoryName;
	}
	
	public java.lang.String getLocalId() {
		return localId;
	}
	public void setLocalId(java.lang.String localId) {
		this.localId = localId;
	}
	public java.lang.String getInventoryName() {
		return inventoryName;
	}
	public void setInventoryName(java.lang.String inventoryName) {
		this.inventoryName = inventoryName;
	}

	public java.lang.String getGlobalId() {
		return globalId;
	}

	public void setGlobalId(java.lang.String globalId) {
		this.globalId = globalId;
	}
    
    
}
