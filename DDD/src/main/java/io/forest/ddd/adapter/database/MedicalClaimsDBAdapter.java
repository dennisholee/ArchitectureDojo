package io.forest.ddd.adapter.database;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.forest.ddd.domain.claim.model.MedicalClaims;
import io.forest.ddd.port.MedicalClaimsRepository;

@Component
public class MedicalClaimsDBAdapter implements MedicalClaimsRepository {

	private MedicalClaimRepository repository;

	public MedicalClaimsDBAdapter(MedicalClaimRepository repository) {
		this.repository = repository;
	}

	@Override
	public void save(MedicalClaims claims) {

		ClaimsEntity claimsEntity = new ClaimsEntity();

		repository.save(claimsEntity);
	}

	@Override
	public Optional<ClaimsEntity> findById(String id) {
		return repository.findById(id);
	}

}
