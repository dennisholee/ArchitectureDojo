package io.forest.ddd.common.domain.event;

public class InvalidDomainEventSubscriber extends RuntimeException {

	private static final long serialVersionUID = -5828741763366547228L;
	
	public InvalidDomainEventSubscriber(String message) {
		super(message);
	}

}
