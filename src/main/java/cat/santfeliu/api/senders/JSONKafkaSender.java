package cat.santfeliu.api.senders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cat.santfeliu.api.components.ConnectorSender;
import cat.santfeliu.api.utils.ConfigProperty;

public class JSONKafkaSender extends ConnectorSender {

	private static final Logger log = LoggerFactory.getLogger(JSONKafkaSender.class);

	@Autowired
	protected KafkaTemplate<String, String> template;

	@ConfigProperty(name = "topic.name", description = "Kafka topic name to send to")
	String topicName;

	@ConfigProperty(name = "topic.partitions", description = "Partitions of topic kafka")
	String partitions;

	@Override
	public void send(JsonNode node) {
		String message = "";
		try {		
			message = new ObjectMapper().writeValueAsString(node);
			this.template.send(topicName, message);
			log.debug("send@JSONKafkaSender - {} - sent to topic {} following message :: {}", this.connectorName, topicName, message);
		} catch (JsonProcessingException e) {
			log.error("send@JSONKafkaSender - {} - error sending to topic {} can't create json", this.connectorName, topicName);
		} catch (Exception e) {
			log.error("send@JSONKafkaSender - {} - error sending to topic {} following message :: {}", this.connectorName, topicName, message);
		}
		
	}
}
