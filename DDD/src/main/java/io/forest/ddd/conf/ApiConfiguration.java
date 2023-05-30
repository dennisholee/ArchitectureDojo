package io.forest.ddd.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.forest.ddd.adapter.api.MedicalClaimsApiAdapter;
import io.forest.ddd.port.SubmitClaim;

public class ApiConfiguration {

	@Autowired
	private SubmitClaim submitClaim;

	@Bean
	MedicalClaimsApiAdapter medicalClaimsApiAdapter() {
		return new MedicalClaimsApiAdapter(submitClaim);
	}
}
