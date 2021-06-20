package cat.santfeliu.api.components;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonObject;

import cat.santfeliu.api.repo.GlobalIdRepo;
import cat.santfeliu.api.utils.InventoryUtils;

public abstract class ConnectorTransformer extends ConnectorComponent {
	
	@Autowired
	protected GlobalIdRepo globalIdRepo;
	
	@Autowired
	protected InventoryUtils invUtils;
	
	public abstract JsonObject transform(JsonObject object);
	
}