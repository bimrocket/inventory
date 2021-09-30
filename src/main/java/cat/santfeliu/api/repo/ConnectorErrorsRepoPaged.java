package cat.santfeliu.api.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import cat.santfeliu.api.model.ConnectorExecutionErrorsDb;
import cat.santfeliu.api.model.ConnectorExecutionErrorsDbId;
import cat.santfeliu.api.model.ConnectorExecutionStatsDb;

public interface ConnectorErrorsRepoPaged extends PagingAndSortingRepository<ConnectorExecutionErrorsDb, ConnectorExecutionErrorsDbId>  {
	
	Page<ConnectorExecutionErrorsDb> findByExecutionConnectorNameOrderByExecutionIdDesc(String connectorName, Pageable page);
}