package cat.santfeliu.api.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.python.jline.internal.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import cat.santfeliu.api.model.ConnectorComponentConfigDb;
import cat.santfeliu.api.repo.ConnectorComponentConfigRepo;
import cat.santfeliu.api.senders.LogSender;
import cat.santfeliu.api.service.RegisterBeansDynamically;

@Configuration
public class KafkaConfiguration {


	private static final Logger log = LoggerFactory.getLogger(KafkaConfiguration.class);
	
	@Value(value = "${kafka.bootstrap.address}")
	private String bootstrapAddress;

	@Autowired
	private RegisterBeansDynamically beans;

	@Autowired
	private ConnectorComponentConfigRepo configRepo;

	@Bean
	public KafkaAdmin kafkaAdmin() throws InterruptedException, ExecutionException {
		Map<String, Object> configs = new HashMap<>();
		// Depending on you Kafka Cluster setup you need to configure
		// additional properties!
		log.debug("kafkaAdmin@KafkaConfiguration - get all the topics by key {}", "topic.name");
		List<ConnectorComponentConfigDb> topicsDb = configRepo.findAllByKey("topic.name");
		int i = 1;
		for (ConnectorComponentConfigDb topic : topicsDb) {
			beans.registerBean("kafkaTopic" + i, TopicBuilder.name(topic.getConfigValue()).partitions(1).build());
			i++;
		}
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

		AdminClient client = AdminClient.create(new KafkaAdmin(configs).getConfigurationProperties());

		// Here you get all the consumer groups
		log.debug("kafkaAdmin@KafkaConfiguration - get all the consumer groups");
		List<String> groupIds = client.listConsumerGroups().all().get().stream().map(s -> s.groupId())
				.collect(Collectors.toList());

		// Here you get all the descriptions for the groups
		log.debug("kafkaAdmin@kafkaConfiguration - get all the description for the group");
		Map<String, ConsumerGroupDescription> groups = client.describeConsumerGroups(groupIds).all().get();
		for (final ConsumerGroupDescription groupId : groups.values()) {
			log.info(groupId.toString());
		}
		return new KafkaAdmin(configs);
	}

	@Bean
	public ProducerFactory<String, String> producerFactory() {
		log.debug("producerFactory@KafkaConfiguration - create a new producer factory.");
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public Map<String, Object> producerConfigs() {
		log.debug("producerConfigs@KafkaConfiguration - create a map and put the properties of producerConfig");
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		// See https://kafka.apache.org/documentation/#producerconfigs for more
		// properties
		return props;
	}

	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		log.debug("kafkaTemplate@KafkaConfiguration - create a new kafka template");
		return new KafkaTemplate<String, String>(producerFactory());
	}
}
