package io.forest.ddd.common.domain.event;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DomainEventPublisherTest {

	@Test
	void test() {

		DomainEventPublisher domainEventPublisher = DomainEventPublisher.instance();
		
		DomainEvent event = new DomainEvent() {

			@Override
			public LocalDate createDate() {
				return LocalDate.now();
			}
		};

		DomainEventSubscriber subscriber = new DomainEventSubscriber<DomainEvent>() {
			@Override
			public void handleEvent(DomainEvent event) {
				System.out.println("Hello world");
			}
		};

		DomainEventSubscriber subscriber1 = new DomainEventSubscriber<DomainEvent>() {
			@Override
			public void handleEvent(DomainEvent event) {
				System.out.println("Hello world");
			}
		};

		domainEventPublisher.addSubscriber(subscriber);
		domainEventPublisher.addSubscriber(subscriber1);

		
		domainEventPublisher.publish(event);
	}
}
