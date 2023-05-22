package io.forest.ddd.port;

import java.util.Optional;

import io.forest.ddd.adapter.database.ClaimsEntity;
import io.forest.ddd.domain.claim.model.MedicalClaims;

public interface MedicalClaimsRepository {

	void save(MedicalClaims claims);

	Optional<ClaimsEntity> findById(String id);

}
