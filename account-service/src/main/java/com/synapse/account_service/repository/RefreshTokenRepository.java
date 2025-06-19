package com.synapse.account_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synapse.account_service.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

}
