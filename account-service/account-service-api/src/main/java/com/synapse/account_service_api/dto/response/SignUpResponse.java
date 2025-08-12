package com.synapse.account_service_api.dto.response;

import java.util.UUID;

public record SignUpResponse(
    UUID id,
    String email,
    String username,
    String role
) {
    
}

