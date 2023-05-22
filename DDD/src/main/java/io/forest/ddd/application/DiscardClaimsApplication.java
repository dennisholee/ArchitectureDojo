package io.forest.ddd.application;

import java.util.Optional;

import org.jmolecules.ddd.annotation.Service;

import io.forest.ddd.adapter.database.ClaimsEntity;
import io.forest.ddd.application.dto.ClaimsDTO;
import io.forest.ddd.domain.command.DiscardClaimCommand;
import io.forest.ddd.port.DiscardClaim;
import io.forest.ddd.port.MedicalClaimsRepository;

@Service
public class DiscardClaimsApplication implements DiscardClaim {

	MedicalClaimsRepository medicalClaimsRepository;

	public DiscardClaimsApplication(MedicalClaimsRepository medicalClaimsRepository) {
		this.medicalClaimsRepository = medicalClaimsRepository;
	}

	@Override
	public void discardClaim(ClaimsDTO dto) {
		DiscardClaimCommand discardClaimCommand = new DiscardClaimCommand();

		Optional<ClaimsEntity> claimsEntity = medicalClaimsRepository.findById(dto.getId());
	}
}
