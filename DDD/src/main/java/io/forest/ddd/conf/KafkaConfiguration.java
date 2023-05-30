package io.forest.ddd.conf;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@EnableConfigurationProperties
@ConditionalOnProperty(value = "application.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConfiguration {

	@Bean
	public KafkaProperties kafkaProperties() {
		return new KafkaProperties();
	}

	@Bean
	public ProducerFactory producerFactory(@Autowired KafkaProperties kafkaProperties) {

		Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();

		DefaultKafkaProducerFactory producerFactory = new DefaultKafkaProducerFactory(producerProperties);
		return producerFactory;
	}

	@Bean
	public ConsumerFactory consumerFactory(@Autowired KafkaProperties kafkaProperties) {

		Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();

		DefaultKafkaConsumerFactory consumerFactory = new DefaultKafkaConsumerFactory(consumerProperties);
		return consumerFactory;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
			@Autowired ConsumerFactory consumerFactory) {

		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);
		return factory;
	}

	@Bean
	public KafkaTemplate kafkaTemplate(@Autowired ProducerFactory producerFactory) {
		return new KafkaTemplate(producerFactory);
	}
}
