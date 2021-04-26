package cat.santfeliu.api.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import cat.santfeliu.api.model.SourceModel;
import cat.santfeliu.api.model.SourcesPk;

public interface SourceRepo extends CrudRepository<SourceModel, SourcesPk> {

	@Query(value="select * from sources where source_name = :src", nativeQuery = true)
	List<SourceModel> findBy(@Param("src") String src);
}