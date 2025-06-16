package com.synapse.account_service.service.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synapse.account_service.domain.PrincipalUser;
import com.synapse.account_service.dto.TokenResponse;
import com.synapse.account_service.dto.TokenResult;
import com.synapse.account_service.service.JwtTokenService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenService jwtTokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();

        UUID memberId = principalUser.providerUser().getId();

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new InternalAuthenticationServiceException("사용자에게 권한이 설정되어 있지 않습니다."));
        
        TokenResponse tokenResponse = jwtTokenService.createTokenResponse(memberId.toString(), role);

        TokenResult refreshToken = tokenResponse.refreshToken();
        long maxAge = Duration.between(Instant.now(), refreshToken.expiresAt()).getSeconds();

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.token())
                .maxAge(maxAge)
                .path("/")
                .httpOnly(true)
                .secure(false) // 프로덕션 환경에서는 true로 설정
                .sameSite("None") // 프론트/백엔드 도메인이 다른 경우
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        TokenResult accessToken = tokenResponse.accessToken();
        Map<String, Object> responseBody = Map.of(
                "accessToken", accessToken.token(),
                "expiresAt", accessToken.expiresAt().toEpochMilli()
        );

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
    
}
