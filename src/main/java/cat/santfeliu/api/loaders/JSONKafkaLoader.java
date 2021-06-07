package cat.santfeliu.api.loaders;

import com.google.gson.JsonObject;

import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.enumerator.JSONKafkaLoaderConfigKeys;
import cat.santfeliu.api.service.KafkaConsumerRunner;

public class JSONKafkaLoader extends ConnectorLoader  {

	private KafkaConsumerRunner runner;
	private Thread threatRunner;
	
	@Override
	public JsonObject load(int timeout) {
		if (runner == null) {		
			runner = new KafkaConsumerRunner(this.instance, this.params.getParamValue(JSONKafkaLoaderConfigKeys.KAFKA_GROUP_ID.getKey()),
			this.params.getParamValue(JSONKafkaLoaderConfigKeys.KAFKA_TOPIC_NAME.getKey()));
			threatRunner = new Thread(runner);
			threatRunner.start();
		};
		return runner.getRecord();
	} 

	@Override
	public void stop() {
		if (runner != null) {
			threatRunner.interrupt();
			runner = null;

		}			
		instance.stop();
	}
}
