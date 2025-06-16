package com.synapse.account_service.dto.request;

public record LoginRequest(
    String username, 
    String password
) {
    
}
