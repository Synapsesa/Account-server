package com.synapse.account_service.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synapse.account_service.domain.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    
}
