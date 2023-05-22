package io.forest.ddd.domain.claim.model;

import java.time.LocalDate;

import org.jmolecules.ddd.annotation.AggregateRoot;

@AggregateRoot
public class TravelClaims<T> {

	private T id;
	private LocalDate submissionDate;

	private ClaimsStatus claimsStatus;

	protected TravelClaims() {

	}

	protected TravelClaims(T id) {
		this.id = id;
	}

	public void submitClaims() {

		this.claimsStatus = ClaimsStatus.SUBMITTED;
	}

	public T getId() {
		return id;
	}

	public LocalDate getSubmissionDate() {
		return submissionDate;
	}

	public ClaimsStatus getClaimsStatus() {
		return claimsStatus;
	}

	protected void setId(T id) {
		this.id = id;
	}

	protected void setSubmissionDate(LocalDate submissionDate) {
		this.submissionDate = submissionDate;
	}
}
