package com.synapse.account_service.dto.response;

import com.synapse.account_service.dto.TokenResult;

/**
 * JwtService가 최종적으로 생성하여 반환할 인증 토큰 DTO
 * 
 * @param accessToken  액세스 토큰 정보
 * @param refreshToken 리프레시 토큰 정보
 */
public record TokenResponse(TokenResult accessToken, TokenResult refreshToken) {
    
}
