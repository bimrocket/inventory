package cat.santfeliu.api.components;

import com.google.gson.JsonObject;

public abstract class ConnectorLoader extends ConnectorComponent {

	public abstract JsonObject load(int timeout);
}
