package cat.santfeliu.api.senders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cat.santfeliu.api.components.ConnectorSender;

public class LogSender extends ConnectorSender {

	private static final Logger log = LoggerFactory.getLogger(LogSender.class);
	
	@Override
	public void send(JsonObject node) {
		log.info("recevied :: {}", node.toString());
		log.debug("send@LogSender - send :: {}", node.toString());
	}

	
}
