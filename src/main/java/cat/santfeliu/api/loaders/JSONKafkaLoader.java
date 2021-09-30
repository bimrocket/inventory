package cat.santfeliu.api.loaders;

import java.util.Properties;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;

import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.service.KafkaJsonConsumerRunner;
import cat.santfeliu.api.utils.ConfigProperty;

public class JSONKafkaLoader extends ConnectorLoader  {

	private static final Logger log = LoggerFactory.getLogger(JSONKafkaLoader.class);

	private KafkaJsonConsumerRunner runner;
	private Thread threatRunner;
	

	@ConfigProperty(name = "consumer.group.id", description = "Number of the kafka group to which the loader belongs")
	String groupId;

	@ConfigProperty(name = "consumer.topic.name", description = "Kafka topic number to send to")
	String topicName;
	
	@ConfigProperty(name = "consumer.kafka.address", description = "Kafka bootstrap servers address")
	String bootstrapAddress;
	
	@Override
	public JsonNode load(long timeout) {
		if (runner == null) {		
			Properties props = new Properties();
	        props.put("bootstrap.servers", bootstrapAddress);
	        props.put("group.id", groupId);
	        props.put("key.deserializer", StringDeserializer.class.getName());
	        props.put("value.deserializer", StringDeserializer.class.getName());
			runner = new KafkaJsonConsumerRunner(this.instance, topicName, props);
			threatRunner = new Thread(runner);
			threatRunner.start();
		};
		log.debug("load@JSONKafkaLoader - {} - load of kafka consumer runner with timeout {}", this.connectorName, timeout);
		return runner.getRecord();
	} 

	@Override
	public void stop() {
		if (runner != null) {
			threatRunner.interrupt();
			runner = null;

		}			
		instance.stop();
		log.debug("stop@JSONKafkaLoader - {} - stop of kafka consumer runner", this.connectorName);
	}
}
