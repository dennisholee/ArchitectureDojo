package io.forest.ddd.adapter.kafka;

import org.springframework.kafka.core.KafkaTemplate;

import io.forest.ddd.common.domain.event.DomainEvent;
import io.forest.ddd.port.PublishDomainEventMessageGateway;

public class PublishDomainEventKafkaGateway implements PublishDomainEventMessageGateway {

	KafkaTemplate kafkaTemplate;

	PublishDomainEventKafkaGateway(KafkaTemplate kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public void publishEvent(DomainEvent event) {
		kafkaTemplate.send("my-topic", event);
	}

}
