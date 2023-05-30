package io.forest.ddd.adapter.kafka;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.forest.ddd.adapter.kafka.avro.DomainEventAVRO;
import io.forest.ddd.application.dto.DomainEventDTO;
import io.forest.ddd.conf.KafkaConfiguration;

@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(classes = { KafkaConfiguration.class })
@ExtendWith(MockitoExtension.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class PublishDomainEventKafkaGatewayTest {
	@Autowired
	BeanFactory factory;

	@Autowired
	EmbeddedKafkaBroker kafkaEmbededBroker;

	DomainEventDTO domainEvent = mock(DomainEventDTO.class);

	PublishDomainEventKafkaGateway publishDomainEventKafkaGateway;

	@BeforeAll
	public void before() {
		KafkaTemplate kafkaTemplate = factory.getBean(KafkaTemplate.class);

		publishDomainEventKafkaGateway = new PublishDomainEventKafkaGateway(kafkaTemplate);
	}

	@Test
	@EnabledIf(expression = "${application.kafka.enabled}", loadContext = true)
	void testPublishEvent_withDomainEvent_expectSuccess() throws InterruptedException, ExecutionException {

		when(domainEvent.getCreateDate()).thenReturn(LocalDate.now());

		publishDomainEventKafkaGateway.publishEvent(domainEvent);

		Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps("group_id", "false", kafkaEmbededBroker);
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MockKafkaAvroDeserializer.class);
		consumerProperties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "not-used");

		BlockingQueue<ConsumerRecord<String, byte[]>> records = new LinkedBlockingQueue<>();

		ContainerProperties containerProperties = new ContainerProperties("my-topic");

		DefaultKafkaConsumerFactory<String, Integer> consumerFactory = new DefaultKafkaConsumerFactory<>(
				consumerProperties);

		KafkaMessageListenerContainer container = new KafkaMessageListenerContainer(consumerFactory,
				containerProperties);

		container.setupMessageListener(new MessageListener<String, byte[]>() {
			@Override
			public void onMessage(ConsumerRecord<String, byte[]> record) {
				ByteBuffer byteBuffer = ByteBuffer.wrap(record.value());
				DomainEventAVRO domainEventAVRO;
				try {
					domainEventAVRO = DomainEventAVRO.fromByteBuffer(byteBuffer);

					// DomainEventAVRO domainEventAVRO = record.value();
					LocalDate localDate = LocalDate.ofEpochDay(domainEventAVRO.getCreateDate());

					records.add(record);
				} catch (IOException e) {
					// TODO: Handle exception
				}
			}
		});

		container.start();

		ContainerTestUtils.waitForAssignment(container, kafkaEmbededBroker.getPartitionsPerTopic());

	}
}
