package cat.santfeliu.api.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import cat.santfeliu.api.model.InputOutputIoModel;
import cat.santfeliu.api.model.InventoryModel;


public interface InputOutputIoRepo extends CrudRepository<InputOutputIoModel, Integer> {


}
