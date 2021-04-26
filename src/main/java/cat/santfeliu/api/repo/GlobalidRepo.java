package cat.santfeliu.api.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import cat.santfeliu.api.model.GlobalIdModel;
import cat.santfeliu.api.model.GlobalIdModel.GlobalIdModelId;

public interface GlobalidRepo extends CrudRepository<GlobalIdModel, GlobalIdModelId> {

	@Query(value="select * from global_ids where inventory_id = :inv", nativeQuery = true)
	List<GlobalIdModel> findByInventory(@Param("inv") Integer inventory);
	
	@Query(value="select * from global_ids where inventory_id = :inv and local_id = :localId", nativeQuery = true)
	Optional<GlobalIdModel> findByInventoryAndLocalId(@Param("inv") Integer inventory, @Param("localId") String localId);
}
