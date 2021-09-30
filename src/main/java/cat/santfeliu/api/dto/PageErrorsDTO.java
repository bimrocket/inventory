package cat.santfeliu.api.dto;

import java.util.ArrayList;
import java.util.List;

public class PageErrorsDTO {

	private int page;
	private int size;
	private int totalPages;
	private int totalELements;
	
	private List<ConnectorErrorsDTO> errors = new ArrayList<>();
	
	public PageErrorsDTO() {
	}
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	

	public List<ConnectorErrorsDTO> getErrors() {
		return errors;
	}

	public void setErrors(List<ConnectorErrorsDTO> errors) {
		this.errors = errors;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getTotalELements() {
		return totalELements;
	}

	public void setTotalELements(int totalELements) {
		this.totalELements = totalELements;
	}
	
}
