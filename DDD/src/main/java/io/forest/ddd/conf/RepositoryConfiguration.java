package io.forest.ddd.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import io.forest.ddd.adapter.database.ClaimsQueryStore;
import io.forest.ddd.adapter.database.MedicalClaimRepository;
import io.forest.ddd.adapter.database.MedicalClaimsDBAdapter;


public class RepositoryConfiguration {

//	@Autowired(required = true)
//	MedicalClaimRepository medicalClaimsRepository;

// 	@Autowired
//	ClaimsQueryStore claimsQueryStore;

//	@Autowired
//	MongoTemplate mongoTemplate;

//	@Bean
//	public MedicalClaimsDBAdapter medicalClaimsDBAdapter(@Autowired MedicalClaimRepository medicalClaimsRepository) {
//		return new MedicalClaimsDBAdapter(medicalClaimsRepository);
//	}

	@Bean
	public ClaimsQueryStore claimsQueryStore(@Autowired MongoTemplate mongoTemplate) {
		return new ClaimsQueryStore(mongoTemplate);
	}

}
