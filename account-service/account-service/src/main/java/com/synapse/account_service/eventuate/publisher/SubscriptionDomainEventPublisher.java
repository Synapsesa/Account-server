package com.synapse.account_service.eventuate.publisher;

import com.synapse.account_service.domain.entity.Subscription;
import com.synapse.account_service_api.event.SubscriptionDomainEvent;

import io.eventuate.tram.events.aggregates.AbstractAggregateDomainEventPublisher;
import io.eventuate.tram.events.publisher.DomainEventPublisher;

public class SubscriptionDomainEventPublisher extends AbstractAggregateDomainEventPublisher<Subscription, SubscriptionDomainEvent>{

    public SubscriptionDomainEventPublisher(DomainEventPublisher eventPublisher) {
        super(eventPublisher, Subscription.class, Subscription::getId);
    }
    
}
