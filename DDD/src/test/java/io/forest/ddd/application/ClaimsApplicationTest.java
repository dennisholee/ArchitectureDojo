package io.forest.ddd.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import io.forest.ddd.application.dto.ClaimsDTO;
import io.forest.ddd.domain.claim.model.MedicalClaims;
import io.forest.ddd.port.MedicalClaimsRepository;

@ExtendWith(MockitoExtension.class)
public class ClaimsApplicationTest {

	@Autowired(required = true)
	private MedicalClaimsRepository medicalClaimsRepository;

	@Autowired(required = true)
	SubmitClaimsApplication claimsApplication;

	@Test
	void testSubmitClaims() {
		LocalDate consultationDate = LocalDate.now();
		LocalDate submissionDate = LocalDate.now();

		ClaimsDTO dto = new ClaimsDTO();
		dto.setConsultationDate(consultationDate);
		dto.setSubmissionDate(submissionDate);

		claimsApplication.submitClaim(dto);

		verify(medicalClaimsRepository, times(1)).save(any(MedicalClaims.class));
	}

}
