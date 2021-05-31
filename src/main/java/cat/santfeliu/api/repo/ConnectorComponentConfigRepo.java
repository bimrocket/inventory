package cat.santfeliu.api.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import cat.santfeliu.api.model.ConnectorComponentConfigDb;
import cat.santfeliu.api.model.ConnectorComponentConfigDbId;

public interface ConnectorComponentConfigRepo extends CrudRepository<ConnectorComponentConfigDb, ConnectorComponentConfigDbId>  {

	@Query(value = "SELECT * FROM connector_component_config WHERE connector_name = :connectorName AND component_type = :componentType", nativeQuery = true)
	List<ConnectorComponentConfigDb> findAllByProducerAndType(@Param("connectorName") String connectorName, @Param("componentType") String componentType);
	
	@Query(value = "SELECT * FROM connector_component_config WHERE connector_name = :connectorName", nativeQuery = true)
	List<ConnectorComponentConfigDb> findAllByProducer(@Param("connectorName") String connectorName);
	
	@Query(value = "SELECT * FROM connector_component_config WHERE config_key = :configKey", nativeQuery = true)
	List<ConnectorComponentConfigDb> findAllByKey(@Param("configKey") String configKey);

}
