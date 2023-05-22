package io.forest.ddd.application.dto;

import java.time.LocalDate;

public class ClaimsDTO {

	private String id;

	private LocalDate consultationDate;

	private LocalDate submissionDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDate getConsultationDate() {
		return consultationDate;
	}

	public void setConsultationDate(LocalDate consultationDate) {
		this.consultationDate = consultationDate;
	}

	public LocalDate getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(LocalDate submissionDate) {
		this.submissionDate = submissionDate;
	}
}
