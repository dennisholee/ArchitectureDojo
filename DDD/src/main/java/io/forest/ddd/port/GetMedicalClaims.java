package io.forest.ddd.port;

import io.forest.ddd.application.dto.ClaimsDTO;

public interface GetMedicalClaims {
	
	ClaimsDTO getMedicalClaims(String claimId);

}
