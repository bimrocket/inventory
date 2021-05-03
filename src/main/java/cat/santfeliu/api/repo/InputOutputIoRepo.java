package cat.santfeliu.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import cat.santfeliu.api.model.InputOutputIoModel;

@RepositoryRestResource(exported = true)
public interface InputOutputIoRepo extends CrudRepository<InputOutputIoModel, Integer> {


}
