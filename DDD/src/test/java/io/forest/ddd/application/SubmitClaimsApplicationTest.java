package io.forest.ddd.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.forest.ddd.adapter.database.MedicalClaimsDBAdapter;
import io.forest.ddd.application.dto.ClaimsDTO;
import io.forest.ddd.conf.ApplicationConfiguration;
import io.forest.ddd.conf.RepositoryConfiguration;
import io.forest.ddd.domain.claim.model.MedicalClaims;
import io.forest.ddd.port.MedicalClaimsRepository;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = { ApplicationConfiguration.class, RepositoryConfiguration.class })
public class SubmitClaimsApplicationTest {

	@Autowired(required = true)
	private MedicalClaimsRepository medicalClaimsRepository;

	@Autowired(required = true)
	SubmitClaimsApplication claimsApplication;

	@Autowired
	BeanFactory beanFactory;
	
	@Mock
	MedicalClaimsDBAdapter medicalClaimsDBAdapter;

	@Test
	void testSubmitClaims() {

		LocalDate consultationDate = LocalDate.now();
		LocalDate submissionDate = LocalDate.now();

		ClaimsDTO dto = new ClaimsDTO();
		dto.setConsultationDate(consultationDate);
		dto.setSubmissionDate(submissionDate);

//		SubmitClaimsApplication claimsApplication = beanFactory.getBean(SubmitClaimsApplication.class);
		claimsApplication.submitClaim(dto);

//		MedicalClaimsDBAdapter medicalClaimsDBAdapter = beanFactory.getBean(MedicalClaimsDBAdapter.class);

		verify(medicalClaimsDBAdapter, times(1)).save(any(MedicalClaims.class));
	}

}
