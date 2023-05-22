package io.forest.ddd.port;

import io.forest.ddd.application.dto.ClaimsDTO;

public interface DiscardClaim {
	
	void discardClaim(ClaimsDTO claimDto);

}
