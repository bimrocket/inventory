package cat.santfeliu.api.dto;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class CentresXMLResponse {

	private String tipus;
	private List<JsonNode> centre;

	
	public CentresXMLResponse() {
	}

	public List<JsonNode> getCentre() {
		return centre;
	}

	public void setCentre(List<JsonNode> centre) {
		this.centre = centre;
	}

	public String getTipus() {
		return tipus;
	}

	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	
	
	
	
}
