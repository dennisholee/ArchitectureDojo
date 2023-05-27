package io.forest.ddd.common.domain.event;

public interface DomainEventSubscriber<T extends DomainEvent> {

	public void handleEvent(T event);
	
	public Class<T> subscribedToEventType();

}
