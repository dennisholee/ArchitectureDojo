package io.forest.ddd.domain.claim.model;

import java.time.LocalDate;
import java.util.UUID;

@org.jmolecules.ddd.annotation.Factory
public class ClaimsFactory implements Factory<MedicalClaims> {

	private LocalDate submissionDate;
	private Clinic clinic;
	private LocalDate consultationDate;
	private Receipt receipt;

	public ClaimsFactory() {

	}

	public ClaimsFactory withSubmissionDate(LocalDate submissionDate) {
		this.submissionDate = submissionDate;
		return this;
	}

	public ClaimsFactory withConsultationDate(LocalDate consultationDate) {
		this.consultationDate = consultationDate;
		return this;
	}

	public ClaimsFactory withMedicalReceipt(Clinic clinic, Receipt receipt) {
		this.clinic = clinic;
		this.receipt = receipt;

		return this;
	}

	@Override
	public MedicalClaims create() {
		UUID id = UUID.randomUUID();

		MedicalClaims claims = new MedicalClaims(id);
		claims.setSubmissionDate(this.submissionDate);
		claims.setClinic(this.clinic);
		claims.setConsultationDate(this.consultationDate);
		claims.setReceipt(this.receipt);
		return claims;
	}

}
