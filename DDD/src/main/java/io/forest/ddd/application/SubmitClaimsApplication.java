package io.forest.ddd.application;

import io.forest.ddd.application.dto.ClaimsDTO;
import io.forest.ddd.common.domain.event.DomainEventPublisher;
import io.forest.ddd.common.domain.event.DomainEventSubscriber;
import io.forest.ddd.domain.claim.model.ClaimsFactory;
import io.forest.ddd.domain.claim.model.MedicalClaims;
import io.forest.ddd.domain.command.SubmitClaimCommand;
import io.forest.ddd.domain.event.SubmitClaimsCreatedEvent;
import io.forest.ddd.port.MedicalClaimsRepository;
import io.forest.ddd.port.SubmitClaim;

public class SubmitClaimsApplication implements SubmitClaim {

	MedicalClaimsRepository medicalClaimsRepository;

	public SubmitClaimsApplication(MedicalClaimsRepository medicalClaimsRepository) {
		this.medicalClaimsRepository = medicalClaimsRepository;
	}

	@Override
	public void submitClaim(ClaimsDTO dto) {

		DomainEventPublisher eventPublisher = DomainEventPublisher.instance();

		DomainEventSubscriber<SubmitClaimsCreatedEvent> subscriber = new DomainEventSubscriber<SubmitClaimsCreatedEvent>() {

			@Override
			public void handleEvent(SubmitClaimsCreatedEvent event) {
				MedicalClaims claims = event.getClaims();
				medicalClaimsRepository.save(claims);
			}
		};
		eventPublisher.addSubscriber(subscriber);

		SubmitClaimCommand submitClaimCommand = new SubmitClaimCommand();

		MedicalClaims claims = new ClaimsFactory().withConsultationDate(dto.getConsultationDate())
				.withSubmissionDate(dto.getSubmissionDate())
				.create();

		claims.executeCommand(submitClaimCommand);
	}
}
