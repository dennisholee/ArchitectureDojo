package io.forest.ddd.application.event.subscriber;

import io.forest.ddd.common.domain.event.DomainEventSubscriber;
import io.forest.ddd.domain.claim.model.MedicalClaims;
import io.forest.ddd.domain.event.SubmitClaimsCreatedEvent;
import io.forest.ddd.port.MedicalClaimsRepository;

public class PersistAggregateEventHandler implements DomainEventSubscriber<SubmitClaimsCreatedEvent> {

	MedicalClaimsRepository medicalClaimsRepository;

	public PersistAggregateEventHandler(MedicalClaimsRepository medicalClaimsRepository) {
		this.medicalClaimsRepository = medicalClaimsRepository;
	}

	@Override
	public void handleEvent(SubmitClaimsCreatedEvent event) {
		MedicalClaims claims = event.getClaims();
		medicalClaimsRepository.save(claims);
	}

	@Override
	public Class<SubmitClaimsCreatedEvent> subscribedToEventType() {
		return SubmitClaimsCreatedEvent.class;
	}

}
