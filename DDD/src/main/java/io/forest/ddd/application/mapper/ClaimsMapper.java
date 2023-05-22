package io.forest.ddd.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.forest.ddd.application.dto.ClaimsDTO;
import io.forest.ddd.domain.claim.model.MedicalClaims;

@Mapper
public interface ClaimsMapper {

	@Mapping(target = "consultationDate", source = "consultationDate")
    @Mapping(target = "submissionDate", source = "submissionDate")
	public MedicalClaims claimsDtoToClaims(ClaimsDTO claimsDto);
	
	
	
}
