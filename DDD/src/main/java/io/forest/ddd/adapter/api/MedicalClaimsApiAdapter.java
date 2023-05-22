package io.forest.ddd.adapter.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.forest.ddd.adapter.api.dto.Claim;
import io.forest.ddd.application.dto.ClaimsDTO;
import io.forest.ddd.port.SubmitClaim;

public class MedicalClaimsApiAdapter implements MedicalClaimsApiDelegate {

	private SubmitClaim submitClaim;

	public MedicalClaimsApiAdapter(SubmitClaim submitClaim) {
		this.submitClaim = submitClaim;
	}

	@Override
	public ResponseEntity<Claim> addClaim(Claim claim) {
		MedicalClaimsMapper mapper = (MedicalClaimsMapper) new MedicalClaimsMapperImpl();

		ClaimsDTO claimDTO = mapper.claimToClaimsDTO(claim);

		// claimsApplication.submitClaims(claimDTO);
		submitClaim.submitClaim(claimDTO);

		return new ResponseEntity<Claim>(HttpStatus.OK).ok(null);
	}

	// @Override
	public ResponseEntity<Claim> discardClaim(Long claimId) {
		ClaimsDTO dto = new ClaimsDTO();
		// dto.setId(claimId);

		// claimsApplication.discardClaims(dto);

		return null;
	}

	public void setSubmitClaim(SubmitClaim submitClaim) {
		this.submitClaim = submitClaim;
	}
}
