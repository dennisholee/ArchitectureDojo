package io.forest.ddd.domain.claim.model;

import java.util.UUID;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class ClaimsId {

	private final UUID id;
	
	public ClaimsId(UUID id) {
		this.id = id;
	}
	
	public UUID getId() {
		return this.id;
	}
}
