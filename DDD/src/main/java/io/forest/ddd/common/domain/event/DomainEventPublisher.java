package io.forest.ddd.common.domain.event;

import java.util.ArrayList;
import java.util.List;

public class DomainEventPublisher {

	private List<DomainEventSubscriber> subscribers;

	private Boolean isPublishing = Boolean.FALSE;

	private final static ThreadLocal<DomainEventPublisher> instance = new ThreadLocal<DomainEventPublisher>() {
		@Override
		protected DomainEventPublisher initialValue() {
			return new DomainEventPublisher();
		}
	};

	public static DomainEventPublisher instance() {
		return instance.get();
	}

	private DomainEventPublisher() {
		this.subscribers = new ArrayList<DomainEventSubscriber>();
	}

	public void addSubscriber(DomainEventSubscriber<?> subscriber) {
		this.subscribers.add(subscriber);
	}

	public void publish(DomainEvent event) {
		if (this.isPublishing)
			return;

		this.isPublishing = Boolean.TRUE;

		try {
			this.subscribers.stream()
					.filter(s -> s.subscribedToEventType() == event.getClass())
					.forEach(s -> {
						s.handleEvent(event);
					});
		} finally {
			this.isPublishing = Boolean.FALSE;
		}
	}
}
