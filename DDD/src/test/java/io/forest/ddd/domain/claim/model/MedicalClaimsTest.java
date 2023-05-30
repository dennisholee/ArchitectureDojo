package io.forest.ddd.domain.claim.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import io.forest.ddd.common.domain.event.DomainEventPublisher;
import io.forest.ddd.domain.command.SubmitClaimCommand;
import io.forest.ddd.domain.event.SubmitClaimsCreatedEvent;
import io.forest.ddd.domain.event.SubmitClaimsRejectedEvent;

@TestInstance(Lifecycle.PER_CLASS)
class MedicalClaimsTest {

	MockedStatic<DomainEventPublisher> mockDomainEventPublisher = mockStatic(DomainEventPublisher.class);

	DomainEventPublisher domainEventPublisher = mock(DomainEventPublisher.class);

	@BeforeAll
	public void before() {
		mockDomainEventPublisher.when(DomainEventPublisher::instance)
				.thenReturn(domainEventPublisher);
	}

	@Test
	public void testHandleCommand_withValidConsultationDate_expectSubmittedStatus() {

		LocalDate consultationDate = LocalDate.now();
		LocalDate submissionDate = LocalDate.now();

		SubmitClaimCommand command = new SubmitClaimCommand();

		MedicalClaims claims = new ClaimsFactory().withConsultationDate(consultationDate)
				.withSubmissionDate(submissionDate)
				.withMedicalReceipt(null, null)
				.create();

		claims.executeCommand(command);

		assertThat(claims).returns(ClaimsStatus.SUBMITTED, from(MedicalClaims::getClaimsStatus))
				.returns(consultationDate, from(MedicalClaims::getConsultationDate))
				.returns(submissionDate, from(MedicalClaims::getSubmissionDate));

		Mockito.verify(domainEventPublisher, times(1))
				.publish(any(SubmitClaimsCreatedEvent.class));
	}

	@Test
	public void testHandleCommand_withInvalidConsultationDate_expectRejectedEvent() {

		LocalDate consultationDate = LocalDate.now()
				.minusDays(30);
		LocalDate submissionDate = LocalDate.now();

		SubmitClaimCommand command = new SubmitClaimCommand();

		MedicalClaims claims = new ClaimsFactory().withConsultationDate(consultationDate)
				.withSubmissionDate(submissionDate)
				.withMedicalReceipt(null, null)
				.create();

		claims.executeCommand(command);

		assertThat(claims).returns(ClaimsStatus.REJECTED, from(MedicalClaims::getClaimsStatus))
				.returns(consultationDate, from(MedicalClaims::getConsultationDate))
				.returns(submissionDate, from(MedicalClaims::getSubmissionDate));

		Mockito.verify(domainEventPublisher, times(1))
				.publish(any(SubmitClaimsRejectedEvent.class));

	}
}
