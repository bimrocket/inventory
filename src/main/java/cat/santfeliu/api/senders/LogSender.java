package cat.santfeliu.api.senders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import cat.santfeliu.api.components.ConnectorSender;

public class LogSender extends ConnectorSender {

	private static final Logger log = LoggerFactory.getLogger(LogSender.class);
	
	@Override
	public void send(JsonNode node) {
		log.info("recevied :: {}", node.asText());
		log.debug("send@LogSender - send :: {}", node.asText());
	}

	
}
