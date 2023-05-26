package io.forest.ddd.port;

import io.forest.ddd.common.domain.event.DomainEvent;

public interface PublishDomainEventMessageGateway<T extends DomainEvent> {

	public void publishEvent(T event);
}
