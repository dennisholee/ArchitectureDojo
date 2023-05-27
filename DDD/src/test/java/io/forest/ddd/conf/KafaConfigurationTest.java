package io.forest.ddd.conf;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.EnabledIf;

@SpringBootTest(classes = { KafkaConfiguration.class })
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class KafaConfigurationTest {

	@Autowired
	BeanFactory factory;

	@Autowired
	EmbeddedKafkaBroker kafkaEmbededBroker;

	@Disabled("Testcase is under development")
	@Test
	@EnabledIf(expression = "${application.kafka.enabled}", loadContext = true)
	void testPing_ExpectPingSuccess() throws InterruptedException, ExecutionException {

		Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps("group_id", "false", kafkaEmbededBroker);
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

		KafkaTemplate kafkaTemplate = factory.getBean(KafkaTemplate.class);

		assertThat(kafkaTemplate).withFailMessage("Unable to load spring factory")
				.isNotNull();

		String message = "Hello World";

		kafkaTemplate.send("my-topic", message);

		BlockingQueue<ConsumerRecord<String, String>> records = new LinkedBlockingQueue<>();

		ContainerProperties containerProperties = new ContainerProperties("my-topic");

		DefaultKafkaConsumerFactory<String, Integer> consumerFactory = new DefaultKafkaConsumerFactory<>(
				consumerProperties);

		KafkaMessageListenerContainer container = new KafkaMessageListenerContainer(consumerFactory,
				containerProperties);

		container.setupMessageListener(new MessageListener<String, String>() {
			@Override
			public void onMessage(ConsumerRecord<String, String> record) {
				System.out.println(String.format("test-listener received message = %s", record.toString()));
				records.add(record);
			}
		});

		container.start();

		ContainerTestUtils.waitForAssignment(container, kafkaEmbededBroker.getPartitionsPerTopic());

	}
}