package io.forest.ddd.common.domain.event;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DomainEventPublisherTest {

	DomainEventPublisher domainEventPublisher = DomainEventPublisher.instance();

	DomainEventSubscriber domainEventSubscriber = mock(DomainEventSubscriber.class);

	DomainEvent domainEvent = mock(DomainEvent.class);

	@Test
	void testPublish_withSubscribedDomainEvent_expectSuccess() {
		when(domainEventSubscriber.subscribedToEventType()).thenReturn(domainEvent.getClass());
		domainEventPublisher.addSubscriber(domainEventSubscriber);

		domainEventPublisher.publish(domainEvent);

		verify(domainEventSubscriber, times(1)).handleEvent(any(domainEvent.getClass()));
	}
	
	@Test
	void testPublish_withOtherDomainEvent_expectIgnore() {
		when(domainEventSubscriber.subscribedToEventType()).thenReturn(DomainEvent.class);
		domainEventPublisher.addSubscriber(domainEventSubscriber);

		domainEventPublisher.publish(domainEvent);

		verify(domainEventSubscriber, never()).handleEvent(any(domainEvent.getClass()));
	}

	@Test
	void testAddSubscriber_withNullSubscriber_expectRejectSubscriber() {
		assertThatExceptionOfType(InvalidDomainEventSubscriber.class).isThrownBy(() -> {
			domainEventPublisher.addSubscriber(null);
		});
	}
}
