package com.synapse.account_service.dto;

import org.springframework.http.ResponseCookie;

public record RefreshTokenResponse(
    ResponseCookie cookie,
    AccessTokenResponse responseBody
) {
    
}
