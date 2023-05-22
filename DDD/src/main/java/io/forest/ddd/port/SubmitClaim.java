package io.forest.ddd.port;

import io.forest.ddd.application.dto.ClaimsDTO;

public interface SubmitClaim {

	void submitClaim(ClaimsDTO claimDTO);

}
