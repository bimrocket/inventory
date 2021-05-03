package cat.santfeliu.api.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import cat.santfeliu.api.model.InventoryModel;

@RepositoryRestResource(exported = true)	
public interface InventoryRepo extends CrudRepository<InventoryModel, Integer> {


	@Query(value = "SELECT * FROM inventories WHERE inventory_job_api_id = :jobApiId", nativeQuery = true)
	public Optional<InventoryModel> findJob(@Param("jobApiId") String job);
	
	@Query(value = "SELECT * FROM inventories WHERE inventory_name = :invName", nativeQuery = true)
	public Optional<InventoryModel> findInvByName(@Param("invName") String invName);
}
