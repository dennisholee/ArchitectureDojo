package io.forest.ddd.adapter.database;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.forest.ddd.domain.claim.model.MedicalClaims;
import io.forest.ddd.port.MedicalClaimsRepository;

@Component
public class MedicalClaimsDBAdapter implements MedicalClaimsRepository {

	private ClaimsRepository repository;

	public MedicalClaimsDBAdapter(ClaimsRepository repository) {
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
