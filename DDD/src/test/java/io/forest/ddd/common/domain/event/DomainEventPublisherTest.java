package io.forest.ddd.common.domain.event;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DomainEventPublisherTest {

	@Test
	void test() {

		DomainEventPublisher domainEventPublisher = DomainEventPublisher.instance();

		DomainEventSubscriber<TestDomainEvent> subscriber = new DomainEventSubscriber<TestDomainEvent>() {
			@Override
			public void handleEvent(TestDomainEvent event) {
				System.out.println("Hello world");
			}

			@Override
			public Class<TestDomainEvent> subscribedToEventType() {

				return TestDomainEvent.class;
			}
		};

		domainEventPublisher.addSubscriber(subscriber);

		TestDomainEvent event = new TestDomainEvent();
		domainEventPublisher.publish(event);
	}
}

class TestDomainEvent implements DomainEvent {

	@Override
	public LocalDate createDate() {
		return LocalDate.now();
	}
};
