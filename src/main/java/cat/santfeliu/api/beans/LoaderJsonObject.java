package cat.santfeliu.api.beans;

import com.fasterxml.jackson.databind.JsonNode;

public class LoaderJsonObject {

	private String globalId;
	private JsonNode element;
	
	public LoaderJsonObject() {
		
	}
	
	public String getGlobalId() {
		return globalId;
	}
	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}
	public JsonNode getElement() {
		return element;
	}
	public void setElement(JsonNode element) {
		this.element = element;
	}
	
	
}
