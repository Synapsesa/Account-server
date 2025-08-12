package com.synapse.account_service_api.dto.response;

import com.synapse.account_service_api.dto.TokenResult;

public record TokenResponse(
    TokenResult accessToken, 
    TokenResult refreshToken
) {
    
}
