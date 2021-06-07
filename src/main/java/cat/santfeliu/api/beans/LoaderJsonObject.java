package cat.santfeliu.api.beans;

import com.google.gson.JsonObject;

public class LoaderJsonObject {

	private String globalId;
	private JsonObject element;
	
	public LoaderJsonObject() {
		
	}
	
	public String getGlobalId() {
		return globalId;
	}
	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}
	public JsonObject getElement() {
		return element;
	}
	public void setElement(JsonObject element) {
		this.element = element;
	}
	
	
}
