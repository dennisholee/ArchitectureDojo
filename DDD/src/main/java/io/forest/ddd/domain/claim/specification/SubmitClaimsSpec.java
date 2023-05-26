package io.forest.ddd.domain.claim.specification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import io.forest.ddd.common.specification.Specification;
import io.forest.ddd.domain.claim.model.MedicalClaims;

public class SubmitClaimsSpec implements Specification<MedicalClaims> {

	private final static int RECEIPT_VALIDITY_PERIOD_IN_DAYS = 28;

	@Override
	public boolean isSatisfied(MedicalClaims claims) {

		LocalDate consultationDate = claims.getConsultationDate();

		LocalDate currentDate = new Date().toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
		LocalDate receiptValidFrom = currentDate.minusDays(RECEIPT_VALIDITY_PERIOD_IN_DAYS);

		return consultationDate.isAfter(receiptValidFrom) && !consultationDate.isAfter(currentDate);
	}
}
