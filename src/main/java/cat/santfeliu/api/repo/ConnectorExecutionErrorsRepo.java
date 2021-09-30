package cat.santfeliu.api.repo;

import org.springframework.data.repository.CrudRepository;

import cat.santfeliu.api.model.ConnectorExecutionErrorsDb;
import cat.santfeliu.api.model.ConnectorExecutionErrorsDbId;

public interface ConnectorExecutionErrorsRepo extends CrudRepository<ConnectorExecutionErrorsDb, ConnectorExecutionErrorsDbId>  {

}
