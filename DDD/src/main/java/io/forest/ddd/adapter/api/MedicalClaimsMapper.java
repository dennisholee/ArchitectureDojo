package io.forest.ddd.adapter.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.forest.ddd.adapter.api.server.dto.Claim;
import io.forest.ddd.application.dto.ClaimsDTO;

@Mapper
public interface MedicalClaimsMapper {

	@Mapping(target = "consultationDate", source = "consultationDate")
	@Mapping(target = "submissionDate", source = "submissionDate")
	ClaimsDTO claimToClaimsDTO(Claim claim);
}
