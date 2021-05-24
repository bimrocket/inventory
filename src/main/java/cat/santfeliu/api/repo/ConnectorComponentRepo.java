package cat.santfeliu.api.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cat.santfeliu.api.model.ConnectorComponentDb;

public interface ConnectorComponentRepo extends CrudRepository<ConnectorComponentDb, String>  {

	@Query(value = "SELECT * FROM connector_component", nativeQuery = true)
	List<ConnectorComponentDb> findAllComponents();

}
