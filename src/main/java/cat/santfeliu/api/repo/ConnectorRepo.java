package cat.santfeliu.api.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cat.santfeliu.api.model.ConnectorComponentDb;
import cat.santfeliu.api.model.ConnectorDb;

public interface ConnectorRepo extends CrudRepository<ConnectorDb, String> {

}
