package cat.santfeliu.inventari.test.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import cat.santfeliu.api.service.KafkaProducer;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class KafkaTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTest.class);

    @Autowired
    private KafkaProducer producer;

    @Value("${test.topic}")
    private String topic;

}
