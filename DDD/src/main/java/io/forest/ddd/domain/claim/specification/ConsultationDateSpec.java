package io.forest.ddd.domain.claim.specification;

import java.time.LocalDate;

import io.forest.ddd.common.specification.CompositeSpecification;
import io.forest.ddd.domain.claim.model.MedicalClaims;

public class ConsultationDateSpec extends CompositeSpecification<MedicalClaims> {

	private static final long RECEIPT_VALIDITY_PERIOD_IN_DAYS = 28;

	@Override
	public boolean isSatisfied(MedicalClaims claims) {

		LocalDate consultationDate = claims.getConsultationDate();

		if (consultationDate == null) {
			return false;
		}

		LocalDate currentDate = LocalDate.now();
		LocalDate validFrom = currentDate.minusDays(RECEIPT_VALIDITY_PERIOD_IN_DAYS);

		return consultationDate.isAfter(validFrom) && !consultationDate.isAfter(currentDate);
	}

};