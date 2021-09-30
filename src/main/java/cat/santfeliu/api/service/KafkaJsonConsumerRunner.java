package cat.santfeliu.api.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cat.santfeliu.api.components.ConnectorInstance;
import cat.santfeliu.api.utils.InventoryErrorSentinelUtils;

public class KafkaJsonConsumerRunner implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(KafkaJsonConsumerRunner.class);

	private final ObjectMapper mapper = new ObjectMapper();
	private final KafkaConsumer<String, String> consumer;
	private final String topic;
	private final ConnectorInstance instance;
	private final String connectorName;
	public boolean end = false;
	private Queue<String> records = new LinkedList<>();

    /**
     * 
     * @param instance
     * @param groupId
     * @param topic
     */
	public KafkaJsonConsumerRunner(ConnectorInstance instance,  String topic, Properties props) {
		this.topic = topic;
		this.instance = instance;
		this.consumer = new KafkaConsumer<>(props);
		this.connectorName = this.instance.getConnector().getConnectorName();
		log.debug("KafkaConsumer@KafkaConsumerRunner - {} - create new kafkaconsumerRunner with topic {} and properties {}", instance.getConnector().getConnectorName(), topic, props.toString());
	}

	@Override
	public void run() {
		try {
			consumer.subscribe(new ArrayList<>(Arrays.asList(this.topic)));

			while (!instance.shouldEnd()) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				for (ConsumerRecord<String, String> record : records) {
					Map<String, Object> data = new HashMap<>();
					data.put("partition", record.partition());
					data.put("offset", record.offset());
					data.put("value", record.value());
					this.records.add(record.value());
				}
			}
		} catch (WakeupException e) {
			// ignore for shutdown
		} finally {
			consumer.close();
		}
		log.debug("run@KafkaConsumerRunner - {} - end run kafkaconsumer", this.connectorName);
	}

	public void shutdown() {
		consumer.wakeup();
		log.debug("shutdown@KafkaConsumerRunner - {} - shutdown kafkaconsumer", this.connectorName);
	}

	public JsonNode getRecord() {
		log.debug("getRecord@KafkaConsumerRunner - {} - get record", this.connectorName);
		String record = this.records.poll();
		if (record == null) {
			return null;
		} else {
			try {
				return mapper.readTree(record);
			} catch (JsonProcessingException e) {
				log.debug("getRecord@KafkaConsummerRunner - {} - record is not a valid Json :: {}", this.connectorName, record);
				return null;
			}
		}
	}
}