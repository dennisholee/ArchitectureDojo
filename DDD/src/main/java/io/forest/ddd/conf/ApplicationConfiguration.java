package io.forest.ddd.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.forest.ddd.application.SubmitClaimsApplication;
import io.forest.ddd.port.MedicalClaimsRepository;

@Configuration
public class ApplicationConfiguration {

	@Autowired
	MedicalClaimsRepository medicalClaimsRepository;
	
	@Bean
	SubmitClaimsApplication submitClaimsApplication() {
		return new SubmitClaimsApplication(medicalClaimsRepository);
	}
}
