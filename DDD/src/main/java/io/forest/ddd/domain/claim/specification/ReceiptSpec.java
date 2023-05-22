package io.forest.ddd.domain.claim.specification;

import io.forest.ddd.common.specification.CompositeSpecification;
import io.forest.ddd.domain.claim.model.MedicalClaims;

public class ReceiptSpec extends CompositeSpecification<MedicalClaims> {

	@Override
	public boolean isSatisfied(MedicalClaims claims) {
		return true;
	}

}
