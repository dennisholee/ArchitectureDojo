package io.forest.ddd.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.forest.ddd.adapter.database.ClaimsRepository;
import io.forest.ddd.adapter.database.MedicalClaimsDBAdapter;

@Configuration
public class RepositoryConfiguration {

	@Autowired
	ClaimsRepository claimsRepository;
	
	@Bean
	public MedicalClaimsDBAdapter medicalClaimsDBAdapter() {
		return new MedicalClaimsDBAdapter(claimsRepository);
	}
	
}
