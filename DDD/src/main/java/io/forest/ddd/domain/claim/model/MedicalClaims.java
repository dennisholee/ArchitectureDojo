package io.forest.ddd.domain.claim.model;

import java.time.LocalDate;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jmolecules.ddd.annotation.AggregateRoot;

import io.forest.ddd.common.domain.event.DomainEventPublisher;
import io.forest.ddd.common.specification.CompositeSpecification;
import io.forest.ddd.domain.claim.specification.ConsultationDateSpec;
import io.forest.ddd.domain.claim.specification.ReceiptSpec;
import io.forest.ddd.domain.command.SubmitClaimCommand;
import io.forest.ddd.domain.event.SubmitClaimsCreatedEvent;
import io.forest.ddd.domain.event.SubmitClaimsRejectedEvent;

@AggregateRoot
public class MedicalClaims {

	private UUID id;
	private LocalDate submissionDate;
	private Clinic clinic;
	private LocalDate consultationDate;
	private Receipt receipt;

	ClaimsStatus claimsStatus;

	protected MedicalClaims() {

	}

	protected MedicalClaims(UUID id) {
		this.id = id;
	}

	public void executeCommand(SubmitClaimCommand submitClaimCommand) {

		ConsultationDateSpec consultationDateSpec = new ConsultationDateSpec();
		ReceiptSpec receiptSpec = new ReceiptSpec();

		CompositeSpecification<MedicalClaims> specification = consultationDateSpec.and(receiptSpec);

		boolean submitClaimSatified = specification.isSatisfied(this);

		if (submitClaimSatified) {
			this.claimsStatus = ClaimsStatus.SUBMITTED;

			SubmitClaimsCreatedEvent event = new SubmitClaimsCreatedEvent();
			event.setClaims(this);
			DomainEventPublisher.instance()
					.publish(event);
		} else {
			this.claimsStatus = ClaimsStatus.REJECTED;
			
			SubmitClaimsRejectedEvent event = new SubmitClaimsRejectedEvent();
			event.setClaims(this);
			DomainEventPublisher.instance()
					.publish(event);
		}
	}

	public UUID getId() {
		return id;
	}

	public LocalDate getSubmissionDate() {
		return submissionDate;
	}

	public Clinic getClinic() {
		return clinic;
	}

	public LocalDate getConsultationDate() {
		return consultationDate;
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public ClaimsStatus getClaimsStatus() {
		return claimsStatus;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setSubmissionDate(LocalDate submissionDate) {
		this.submissionDate = submissionDate;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	public void setConsultationDate(LocalDate consultationDate) {
		this.consultationDate = consultationDate;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
