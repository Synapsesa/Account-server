package com.synapse.account_service.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.synapse.account_service.dto.TokenResult;
import com.synapse.account_service.dto.response.TokenResponse;
import com.synapse.account_service.exception.ExceptionType;
import com.synapse.account_service.exception.JWTTokenExpiredException;
import com.synapse.account_service.exception.JWTValidationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtTokenTemplate jwtTokenTemplate;

    @Value("${jwt.access.expiration.minutes}")
    private long accessTokenExpirationMinutes;

    @Value("${jwt.refresh.expiration.minutes}")
    private long refreshTokenExpirationMinutes;

    public TokenResponse createTokenResponse(String subject, String role) {
        Map<String, String> accessTokenClaims = Map.of("role", role);

        TokenResult accessToken = jwtTokenTemplate.createToken(subject, accessTokenClaims, accessTokenExpirationMinutes);
        TokenResult refreshToken = jwtTokenTemplate.createToken(subject, null, refreshTokenExpirationMinutes);
        
        return new TokenResponse(accessToken, refreshToken);
    }

    public UUID getMemberIdFrom(String token) {
        try {
            DecodedJWT decodedJWT = jwtTokenTemplate.verifyAndDecode(token);
            return UUID.fromString(decodedJWT.getSubject());
        } catch (TokenExpiredException e) {
            throw new JWTTokenExpiredException(ExceptionType.EXPIRED_TOKEN);
        } catch (JWTVerificationException e) {
            throw new JWTValidationException(ExceptionType.INVALID_TOKEN);
        }
    }

    // 테스트용 메서드
    public String createExpiredTokenForTest(String subject) {
        return jwtTokenTemplate.createExpiredTokenForTest(subject);
    }
} 
