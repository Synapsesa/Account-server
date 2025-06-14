package com.synapse.account_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synapse.account_service.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByProviderAndRegistrationId(String provider, String registrationId);
}
