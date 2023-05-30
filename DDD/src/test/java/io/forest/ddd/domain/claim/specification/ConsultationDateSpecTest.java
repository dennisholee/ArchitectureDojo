package io.forest.ddd.domain.claim.specification;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import io.forest.ddd.domain.claim.model.ClaimsFactory;
import io.forest.ddd.domain.claim.model.MedicalClaims;

class ConsultationDateSpecTest {

	@Test
	void testIsSatifisfied_WithValidConsultationDate_ExpectSuccess() {
		LocalDate consultationDate = LocalDate.now();

		MedicalClaims c = new ClaimsFactory().withConsultationDate(consultationDate)
				.create();

		ConsultationDateSpec spec = new ConsultationDateSpec();

		assertThat(spec.isSatisfied(c)).isTrue();
	}

	@Test
	void testIsSatifisfied_WithExpiredConsultationDate_ExpectFail() {
		LocalDate consultationDate = LocalDate.now()
				.minusDays(30);

		MedicalClaims c = new ClaimsFactory().withConsultationDate(consultationDate)
				.create();

		ConsultationDateSpec spec = new ConsultationDateSpec();

		assertThat(spec.isSatisfied(c)).isFalse();
	}

	@Test
	void testIsSatifisfied_WithFutureConsultationDate_ExpectFail() {
		LocalDate consultationDate = LocalDate.now()
				.plusDays(1);

		MedicalClaims c = new ClaimsFactory().withConsultationDate(consultationDate)
				.create();

		ConsultationDateSpec spec = new ConsultationDateSpec();

		assertThat(spec.isSatisfied(c)).isFalse();
	}

}
