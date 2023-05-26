package io.forest.ddd.conf;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import io.forest.ddd.port.MedicalClaimsRepository;

@Profile("test")
@Configuration
public class RepositoryConfigurationTest {

	@Bean
	@Primary
	public MedicalClaimsRepository medicalClaimRepository() {
		return mock(MedicalClaimsRepository.class);
	}
}
