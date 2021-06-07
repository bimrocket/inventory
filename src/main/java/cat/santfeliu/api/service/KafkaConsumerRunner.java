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
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import cat.santfeliu.api.components.ConnectorInstance;

public class KafkaConsumerRunner implements Runnable {
	private final Gson gson = new Gson();
	private final KafkaConsumer<String, String> consumer;
	private final String topic;
	private final ConnectorInstance instance;
	public boolean end = false;
	private Queue<String> records = new LinkedList<>();
	
    @Value(value = "${kafka.bootstrap.address}")
    private String bootstrapAddress;

	public KafkaConsumerRunner(ConnectorInstance instance, String groupId, String topic) {
		this.topic = topic;
		this.instance = instance;
		Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.0.216:9094");
		props.put("group.id", groupId);
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer", StringDeserializer.class.getName());
		props.put("auto.offset.reset", "earliest");
		this.consumer = new KafkaConsumer<>(props);
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
	}

	public void shutdown() {
		consumer.wakeup();
	}

	public JsonObject getRecord() {
		String record = this.records.poll();
		if (record == null) {
			return null;
		} else {
			return gson.fromJson(record, JsonObject.class);
		}
	}
}