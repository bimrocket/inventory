package cat.santfeliu.api.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import cat.santfeliu.api.model.InventoryModel;


public interface InventoryRepo extends CrudRepository<InventoryModel, Integer> {


	@Query(value = "SELECT * FROM inventories WHERE inventory_job_api_id = :jobApiId", nativeQuery = true)
	public Optional<InventoryModel> findJob(@Param("jobApiId") String job);
}
