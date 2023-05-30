package io.forest.ddd.application;

import io.forest.ddd.application.dto.ClaimsDTO;
import io.forest.ddd.application.event.subscriber.NotificationEventHandler;
import io.forest.ddd.application.event.subscriber.PersistAggregateEventHandler;
import io.forest.ddd.common.domain.event.DomainEventPublisher;
import io.forest.ddd.domain.claim.model.ClaimsFactory;
import io.forest.ddd.domain.claim.model.MedicalClaims;
import io.forest.ddd.domain.command.SubmitClaimCommand;
import io.forest.ddd.port.MedicalClaimsRepository;
import io.forest.ddd.port.PublishDomainEventMessageGateway;
import io.forest.ddd.port.SubmitClaim;

public class SubmitClaimsApplication implements SubmitClaim {

	MedicalClaimsRepository medicalClaimsRepository;

	PublishDomainEventMessageGateway publishDomainEventMessageGateway;

	public SubmitClaimsApplication(MedicalClaimsRepository medicalClaimsRepository,
			PublishDomainEventMessageGateway publishDomainEventMessageGateway) {
		this.medicalClaimsRepository = medicalClaimsRepository;
		this.publishDomainEventMessageGateway = publishDomainEventMessageGateway;
	}

	@Override
	public void submitClaim(ClaimsDTO dto) {

		DomainEventPublisher eventPublisher = DomainEventPublisher.instance();

		eventPublisher.addSubscriber(new PersistAggregateEventHandler(medicalClaimsRepository));
		eventPublisher.addSubscriber(new NotificationEventHandler(publishDomainEventMessageGateway));

		SubmitClaimCommand submitClaimCommand = new SubmitClaimCommand();

		MedicalClaims claims = new ClaimsFactory().withConsultationDate(dto.getConsultationDate())
				.withSubmissionDate(dto.getSubmissionDate())
				.create();

		claims.executeCommand(submitClaimCommand);
	}
}
