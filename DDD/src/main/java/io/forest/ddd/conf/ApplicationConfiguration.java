package io.forest.ddd.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import io.forest.ddd.adapter.kafka.PublishDomainEventKafkaGateway;
import io.forest.ddd.application.SubmitClaimsApplication;
import io.forest.ddd.port.MedicalClaimsRepository;
import io.forest.ddd.port.PublishDomainEventMessageGateway;

public class ApplicationConfiguration {

	@Autowired
	MedicalClaimsRepository medicalClaimsRepository;
	
	@Autowired
	KafkaTemplate kafkaTemplate;
	
	@Bean
	PublishDomainEventMessageGateway publishDomainEventMessageGateway() {
		return new PublishDomainEventKafkaGateway(kafkaTemplate);
	}
	
	@Bean
	SubmitClaimsApplication submitClaimsApplication() {
		return new SubmitClaimsApplication(medicalClaimsRepository, publishDomainEventMessageGateway());
	}
}
