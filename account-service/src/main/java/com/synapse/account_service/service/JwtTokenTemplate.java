package com.synapse.account_service.service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.synapse.account_service.dto.TokenResult;

@Component
public class JwtTokenTemplate {
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtTokenTemplate(@Value("${secret.key}") String secretKey) {
        this.algorithm = Algorithm.HMAC256(secretKey);
        this.verifier = JWT.require(this.algorithm).build();
    }

    public final TokenResult createToken(String subject, Map<String, ?> claims, long expirationMinutes) {
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationMinutes, ChronoUnit.MINUTES);

        String token = JWT.create()
                .withSubject(subject)
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .withPayload(claims)
                .sign(algorithm);

        return new TokenResult(token, expiration);
    }

    public final DecodedJWT verifyAndDecode(String token) throws JWTVerificationException {
        return verifier.verify(token);
    }

    // 테스트용 메서드
    public final String createExpiredTokenForTest(String subject) {
        Instant now = Instant.now();
        Instant past = now.minus(Duration.ofMinutes(10)); // 10분 전 만료

        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(past)
                .withExpiresAt(past)
                .sign(algorithm);
    }
}
