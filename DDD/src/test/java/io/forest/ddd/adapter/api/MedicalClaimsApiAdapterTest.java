package io.forest.ddd.adapter.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.forest.ddd.adapter.api.dto.Claim;
import io.forest.ddd.conf.ApiConfiguration;
import io.forest.ddd.conf.ApplicationConfiguration;
import io.forest.ddd.conf.RepositoryConfigurationTest;
import io.forest.ddd.domain.claim.model.MedicalClaims;
import io.forest.ddd.port.MedicalClaimsRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = { ApiConfiguration.class, ApplicationConfiguration.class, RepositoryConfigurationTest.class })
class MedicalClaimsApiAdapterTest {

	@Mock
	Claim claim;

	@Autowired(required = true)
	MedicalClaimsApiAdapter adapter;

	@Autowired(required = true)
	MedicalClaimsRepository medicalClaimsRepository;

	@Test
	void test() {

		LocalDate consultationDate = LocalDate.now();
		LocalDate submissionDate = LocalDate.now();

		when(claim.getConsultationDate()).thenReturn(consultationDate);
		when(claim.getSubmissionDate()).thenReturn(submissionDate);
		
		adapter.addClaim(claim);
		
		verify(medicalClaimsRepository, times(1)).save(any(MedicalClaims.class));
	}

}
