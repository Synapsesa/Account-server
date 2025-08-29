package com.synapse.account_service.eventuate.configuration;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.spring.jdbckafka.TramJdbcKafkaConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.synapse.account_service.eventuate.publisher.MemberDomainEventPublisher;
import com.synapse.account_service.eventuate.publisher.SubscriptionDomainEventPublisher;

@Configuration
@Import({TramEventsPublisherConfiguration.class, TramJdbcKafkaConfiguration.class})
public class EventuateConfig {
    @Bean
    public MemberDomainEventPublisher memberDomainEventPublisher(DomainEventPublisher eventPublisher) {
        return new MemberDomainEventPublisher(eventPublisher);
    }

    @Bean
    public SubscriptionDomainEventPublisher subscriptionDomainEventPublisher(DomainEventPublisher eventPublisher) {
        return new SubscriptionDomainEventPublisher(eventPublisher);
    }
}
