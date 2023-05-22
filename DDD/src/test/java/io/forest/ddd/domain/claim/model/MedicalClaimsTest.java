package io.forest.ddd.domain.claim.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import io.forest.ddd.domain.command.SubmitClaimCommand;

class MedicalClaimsTest {

	@Test
	public void test_handleCommand_withValidConsultationDate_expectSubmittedStatus() {

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
	}
	
	

}
