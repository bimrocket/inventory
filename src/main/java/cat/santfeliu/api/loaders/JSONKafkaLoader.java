package cat.santfeliu.api.loaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.service.KafkaConsumerRunner;
import cat.santfeliu.api.utils.ConfigProperty;

public class JSONKafkaLoader extends ConnectorLoader  {

	private static final Logger log = LoggerFactory.getLogger(JSONKafkaLoader.class);

	private KafkaConsumerRunner runner;
	private Thread threatRunner;

	@ConfigProperty(name = "consumer.group.id", description = "Number of the kafka group to which the loader belongs")
	String groupID;

	@ConfigProperty(name = "consumer.topic.name", description = "Kafka topic number to send to")
	String topicName;
	
	@Override
	public JsonNode load(long timeout) {
		if (runner == null) {		
			runner = new KafkaConsumerRunner(this.instance, groupID, topicName);
			threatRunner = new Thread(runner);
			threatRunner.start();
		};
		log.debug("load@JSONKafkaLoader - load of kafka consumer runner with timeout {}", timeout);
		return runner.getRecord();
	} 

	@Override
	public void stop() {
		if (runner != null) {
			threatRunner.interrupt();
			runner = null;

		}			
		instance.stop();
		log.debug("stop@JSONKafkaLoader - stop of kafka consumer runner");
	}
}
