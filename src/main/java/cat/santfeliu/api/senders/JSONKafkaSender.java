package cat.santfeliu.api.senders;

import java.util.HashSet;
import java.util.Set;

import cat.santfeliu.api.service.ConnectorRunnerService;
import cat.santfeliu.api.utils.ConfigProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import cat.santfeliu.api.components.ConnectorSender;
import cat.santfeliu.api.enumerator.JSONKafkaSenderConfigKeys;

public class JSONKafkaSender extends ConnectorSender {

	private static final Logger log = LoggerFactory.getLogger(JSONKafkaSender.class);

	@Autowired
	protected KafkaTemplate<String, String> template;

	@Autowired
	protected KafkaAdmin admin;

	@ConfigProperty(name = "topic.name", description = "Kafka topic name to send to")
	String topicName;

	@ConfigProperty(name = "topic.partitions", description = "Partitions of topic kafka")
	String partitions;

	@Override
	public void send(JsonNode node) {
		try {
			this.template.send(topicName,
					new ObjectMapper().writeValueAsString(node));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		try {
			log.debug("send@JSONKafkaSender - send :: {} , {}", topicName, new ObjectMapper().writeValueAsString(node));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

//	private void createTopic(JsonObject node) {
//		// Create topic
//
//		String kafkaTopicName = this.params.getParamValue(JSONKafkaSenderConfigKeys.KAFKA_TOPIC_NAME.getKey());
//		int topicPartitions = Integer
//				.valueOf(this.params.getParamValue(JSONKafkaSenderConfigKeys.KAFKA_PARTITIONS.getKey()));
//		NewTopic topic = TopicBuilder.name(kafkaTopicName).partitions(topicPartitions).build();
//		// Register topic
//		AdminClient client = AdminClient.create(admin.getConfigurationProperties());
//		Set<NewTopic> topics = new HashSet<>();
//		topics.add(topic);
//		client.createTopics(topics).all().thenApply(v -> completedFuture(node));
//	}
//
//	private Object completedInit(JsonObject node) {
//		this.initialized = true;
//
//	}

}
