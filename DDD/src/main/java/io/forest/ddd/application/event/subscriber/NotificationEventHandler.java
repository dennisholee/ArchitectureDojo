package io.forest.ddd.application.event.subscriber;

import io.forest.ddd.common.domain.event.DomainEventSubscriber;
import io.forest.ddd.domain.event.SubmitClaimsCreatedEvent;
import io.forest.ddd.port.PublishDomainEventMessageGateway;

public class NotificationEventHandler implements DomainEventSubscriber<SubmitClaimsCreatedEvent> {

	private PublishDomainEventMessageGateway publishDomainEventMessageGatewaygateway;

	public NotificationEventHandler(PublishDomainEventMessageGateway publishDomainEventMessageGatewaygateway) {
		this.publishDomainEventMessageGatewaygateway = publishDomainEventMessageGatewaygateway;
	}

	@Override
	public void handleEvent(SubmitClaimsCreatedEvent event) {
		this.publishDomainEventMessageGatewaygateway.publishEvent(event);
	}

}
