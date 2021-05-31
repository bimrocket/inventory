package cat.santfeliu.api.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import com.google.gson.JsonObject;

public abstract class ConnectorSender extends ConnectorComponent {

	public abstract void send(JsonObject node);
}
