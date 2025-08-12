package com.synapse.account_service_api.dto;

import java.time.Instant;

public record TokenResult(
    String token, 
    Instant expiresAt
) {
    
}
