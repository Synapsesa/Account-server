package com.synapse.account_service_api.dto.response;

import org.springframework.http.ResponseCookie;

public record RefreshTokenResponse(
    ResponseCookie cookie,
    AccessTokenResponse responseBody
) {
    
}
