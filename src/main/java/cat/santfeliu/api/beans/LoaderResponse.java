package cat.santfeliu.api.beans;

public class LoaderResponse {

	private String format;
	private String response;
	private boolean valid;
	
	public LoaderResponse() {
	}
	
	public LoaderResponse(String format, String response, boolean valid) {
		this.format = format;
		this.response = response;
		this.valid = valid;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	
	
	
}
