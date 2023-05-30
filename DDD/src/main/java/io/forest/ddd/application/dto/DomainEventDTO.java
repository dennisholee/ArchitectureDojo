package io.forest.ddd.application.dto;

import java.time.LocalDate;

public class DomainEventDTO {

	LocalDate createDate;

	public LocalDate getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDate createDate) {
		this.createDate = createDate;
	}

}
