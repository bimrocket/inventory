package cat.santfeliu.api.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.model.GlobalIdDbId;

public interface GlobalIdRepo extends CrudRepository<GlobalIdDb, GlobalIdDbId> {
	
	@Query(value = "SELECT * FROM global_id WHERE inventory_name = :inventoryName AND local_id = :localId", nativeQuery = true)
	Optional<GlobalIdDb> findByInventoryAndLocalId(@Param("inventoryName") String inventoryName, @Param("localId") String localId);

	@Query(value = "SELECT * FROM global_id WHERE inventory_name = :inventoryName AND global_id = :globalId", nativeQuery = true)
	Optional<GlobalIdDb> findByInventoryAndGlobalId(@Param("inventoryName") String inventoryName, @Param("globalId") String globalId);

	@Query(value = "SELECT * FROM global_id WHERE inventory_name = :inventoryName", nativeQuery = true)
	List<GlobalIdDb> findByInventory(@Param("inventoryName") String inventoryName);
}
