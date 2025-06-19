package com.synapse.account_service.dto;

import java.time.Instant;

/**
 * JwtTemplate이 토큰 생성 후 반환할 DTO
 * 
 * @param token     생성된 JWT 문자열
 * @param expiresAt 토큰의 만료 시간
 */
public record TokenResult(
    String token, 
    Instant expiresAt
) {
    
}
