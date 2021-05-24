package cat.santfeliu.api.repo;

import org.springframework.data.repository.CrudRepository;

import cat.santfeliu.api.model.ConnectorDb;

public interface ConnectorRepo extends CrudRepository<ConnectorDb, String> {

}
