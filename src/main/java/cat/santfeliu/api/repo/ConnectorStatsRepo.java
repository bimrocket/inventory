package cat.santfeliu.api.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import cat.santfeliu.api.model.ConnectorExecutionStatsDb;

public interface ConnectorStatsRepo extends PagingAndSortingRepository<ConnectorExecutionStatsDb, Integer>  {
	
	Page<ConnectorExecutionStatsDb> findByExecutionConnectorNameOrderByExecutionStartDateDesc(String connectorName, Pageable page);
}
