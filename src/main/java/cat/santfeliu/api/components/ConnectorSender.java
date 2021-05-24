package cat.santfeliu.api.components;

import com.google.gson.JsonObject;

public abstract class ConnectorSender extends ConnectorComponent {

	public abstract void send(JsonObject node);
}
