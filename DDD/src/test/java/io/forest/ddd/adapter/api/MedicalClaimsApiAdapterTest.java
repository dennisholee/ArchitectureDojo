package io.forest.ddd.adapter.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import io.forest.ddd.adapter.api.server.dto.Claim;
import io.forest.ddd.port.SubmitClaim;

@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class MedicalClaimsApiAdapterTest {

	Claim claim = mock(Claim.class);
	
	SubmitClaim submitClaim = mock(SubmitClaim.class);
	
	MedicalClaimsApiAdapter adapter = new MedicalClaimsApiAdapter(submitClaim);

	
	@Test
	void test() {

		LocalDate consultationDate = LocalDate.now();
		LocalDate submissionDate = LocalDate.now();

		when(claim.getConsultationDate()).thenReturn(consultationDate);
		when(claim.getSubmissionDate()).thenReturn(submissionDate);
		
		adapter.addClaim(claim);
	}

}
