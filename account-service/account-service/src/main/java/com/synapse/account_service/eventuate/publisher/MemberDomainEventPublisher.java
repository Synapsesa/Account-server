package com.synapse.account_service.eventuate.publisher;

import com.synapse.account_service.domain.entity.Member;
import com.synapse.account_service_api.event.MemberDomainEvent;

import io.eventuate.tram.events.aggregates.AbstractAggregateDomainEventPublisher;
import io.eventuate.tram.events.publisher.DomainEventPublisher;

// public class MemberDomainEventPublisher extends AbstractAggregateDomainEventPublisher<Member, MemberDomainEvent> {
    
//     public MemberDomainEventPublisher(DomainEventPublisher eventPublisher) {
//         super(eventPublisher, Member.class, Member::getId);
//     }
// }
