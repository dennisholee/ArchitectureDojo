package io.forest.ddd.port;

import io.forest.ddd.application.dto.DomainEventDTO;

public interface PublishDomainEventMessageGateway<T extends DomainEventDTO> {

	public void publishEvent(T event);
}
