package com.synapse.account_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.synapse.account_service.domain.Member;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByUsername(String username);
    Optional<Member> findByProviderAndRegistrationId(String provider, String registrationId);

    @Query("SELECT m FROM Member m WHERE (m.provider = :provider AND m.registrationId = :registrationId) OR m.email = :email OR m.username = :username")
    Optional<Member> findBySocialIdOrEmailOrUsername(@Param("provider") String provider, @Param("registrationId") String registrationId, @Param("email") String email, @Param("username") String username);
}
