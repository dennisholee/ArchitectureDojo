package io.forest.ddd.common.domain.event;

public interface DomainEventSubscriber<T> {

	public void handleEvent(T event);

}
