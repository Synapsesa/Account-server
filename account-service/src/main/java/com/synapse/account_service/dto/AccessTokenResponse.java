package com.synapse.account_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
