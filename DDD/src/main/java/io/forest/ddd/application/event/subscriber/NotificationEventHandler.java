package io.forest.ddd.application.event.subscriber;

import io.forest.ddd.application.dto.DomainEventDTO;
import io.forest.ddd.common.domain.event.DomainEventSubscriber;
import io.forest.ddd.domain.event.SubmitClaimsCreatedEvent;
import io.forest.ddd.port.PublishDomainEventMessageGateway;

public class NotificationEventHandler implements DomainEventSubscriber<SubmitClaimsCreatedEvent> {

	private PublishDomainEventMessageGateway publishDomainEventMessageGateway;

	public NotificationEventHandler(PublishDomainEventMessageGateway publishDomainEventMessageGateway) {
		this.publishDomainEventMessageGateway = publishDomainEventMessageGateway;
	}

	@Override
	public void handleEvent(SubmitClaimsCreatedEvent event) {
		DomainEventDTO domainEventDTO = new DomainEventDTO();
		domainEventDTO.setCreateDate(event.createDate());

		this.publishDomainEventMessageGateway.publishEvent(domainEventDTO);
	}

	@Override
	public Class<SubmitClaimsCreatedEvent> subscribedToEventType() {
		return SubmitClaimsCreatedEvent.class;
	}

}
