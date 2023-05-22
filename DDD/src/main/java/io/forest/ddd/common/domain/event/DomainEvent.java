package io.forest.ddd.common.domain.event;

import java.time.LocalDate;

public interface DomainEvent {
	
	public LocalDate createDate();

}
