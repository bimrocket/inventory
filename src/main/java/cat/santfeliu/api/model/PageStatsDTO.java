package cat.santfeliu.api.model;

import java.util.ArrayList;
import java.util.List;

import cat.santfeliu.api.dto.ConnectorStatsDTO;

public class PageStatsDTO {

	private int page;
	private int size;
	private int totalPages;
	private int totalELements;
	
	private List<ConnectorStatsDTO> stats = new ArrayList<>();
	
	public PageStatsDTO() {
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
	public List<ConnectorStatsDTO> getStats() {
		return stats;
	}
	public void setStats(List<ConnectorStatsDTO> stats) {
		this.stats = stats;
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
