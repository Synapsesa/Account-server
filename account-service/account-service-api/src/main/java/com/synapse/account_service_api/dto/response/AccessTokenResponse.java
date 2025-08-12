package com.synapse.account_service_api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synapse.account_service_api.dto.TokenResult;

public record AccessTokenResponse(
    @JsonProperty("accessToken") String token,
    @JsonProperty("expiresAt") long expiresAt
) {
    public static AccessTokenResponse from(TokenResult tokenResult) {
        return new AccessTokenResponse(
                tokenResult.token(),
                tokenResult.expiresAt().toEpochMilli());
    }
}

