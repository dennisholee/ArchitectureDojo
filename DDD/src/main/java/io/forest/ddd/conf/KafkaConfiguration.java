package io.forest.ddd.conf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@EnableConfigurationProperties
@ConditionalOnProperty(value = "application.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "spring.kafka.producer")
	public KafkaProperties kafkaProducerProperties() {
		return new KafkaProperties();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.kafka.consumer")
	public KafkaProperties kafkaConsumerProperties() {
		return new KafkaProperties();
	}

	@Bean
	public ProducerFactory producerFactory() {
		KafkaProperties kafkaProperties = kafkaProducerProperties();

		DefaultKafkaProducerFactory producerFactory = new DefaultKafkaProducerFactory(
				kafkaProperties.buildProducerProperties());
		return producerFactory;
	}

	@Bean
	public ConsumerFactory consumerFactory() {
		KafkaProperties kafkaProperties = kafkaConsumerProperties();

		DefaultKafkaConsumerFactory consumerFactory = new DefaultKafkaConsumerFactory(
				kafkaProperties.buildConsumerProperties());
		return consumerFactory;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {

		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}

	@Bean
	public KafkaTemplate kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

//	@Bean
//	public KafkaConsumer kafkaConsumer() {
//		return new KafkaConsumer();
//	}
}
