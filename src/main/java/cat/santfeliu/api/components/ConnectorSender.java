package cat.santfeliu.api.components;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;

import cat.santfeliu.api.repo.GlobalIdRepo;

public abstract class ConnectorSender extends ConnectorComponent {

	@Autowired
	protected GlobalIdRepo globalIdRepo;
	
	public abstract void send(JsonNode node);
}
