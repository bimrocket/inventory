package cat.santfeliu.api.loaders;

import cat.santfeliu.api.service.ConnectorRunnerService;
import com.fasterxml.jackson.databind.JsonNode;

import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.enumerator.JSONKafkaLoaderConfigKeys;
import cat.santfeliu.api.service.KafkaConsumerRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONKafkaLoader extends ConnectorLoader  {

	private static final Logger log = LoggerFactory.getLogger(JSONKafkaLoader.class);

	private KafkaConsumerRunner runner;
	private Thread threatRunner;
	
	@Override
	public JsonNode load(long timeout) {
		if (runner == null) {		
			runner = new KafkaConsumerRunner(this.instance, this.params.getParamValue(JSONKafkaLoaderConfigKeys.KAFKA_GROUP_ID.getKey()),
			this.params.getParamValue(JSONKafkaLoaderConfigKeys.KAFKA_TOPIC_NAME.getKey()));
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
