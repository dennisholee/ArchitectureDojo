package io.forest.ddd.domain.event;

import java.time.LocalDate;

import io.forest.ddd.common.domain.event.DomainEvent;
import io.forest.ddd.domain.claim.model.MedicalClaims;

public class SubmitClaimsCreatedEvent implements DomainEvent {

	private LocalDate createDate;

	private MedicalClaims claims;

	public SubmitClaimsCreatedEvent() {
		this.createDate = LocalDate.now();
	}

	@Override
	public LocalDate createDate() {
		return this.createDate;
	}

	public MedicalClaims getClaims() {
		return this.claims;
	}

	public void setClaims(MedicalClaims medicalClaims) {
		this.claims = medicalClaims;

	}

}
