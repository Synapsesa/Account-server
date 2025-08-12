package com.synapse.account_service.eventuate.configuration;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

// import com.synapse.account_service.eventuate.publisher.MemberDomainEventPublisher;

// @Configuration
// @Import(TramEventsPublisherConfiguration.class)
// public class EventuateConfig {
//     @Bean
//     public MemberDomainEventPublisher memberDomainEventPublisher(DomainEventPublisher eventPublisher) {
//         return new MemberDomainEventPublisher(eventPublisher);
//     }
// }
