package cat.santfeliu.api.repo;

import org.springframework.data.repository.CrudRepository;

import cat.santfeliu.api.model.ConnectorStatusDb;

public interface ConnectorStatusRepo extends CrudRepository<ConnectorStatusDb, String> {

}
