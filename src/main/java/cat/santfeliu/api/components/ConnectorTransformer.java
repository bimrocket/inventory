package cat.santfeliu.api.components;

import com.google.gson.JsonObject;

public abstract class ConnectorTransformer extends ConnectorComponent {

	public abstract JsonObject transform(JsonObject object);
}