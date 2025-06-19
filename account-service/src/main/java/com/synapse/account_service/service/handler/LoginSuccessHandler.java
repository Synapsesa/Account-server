package com.synapse.account_service.service.handler;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.synapse.account_service.domain.PrincipalUser;
import com.synapse.account_service.dto.response.TokenResponse;
import com.synapse.account_service.service.JwtTokenService;
import com.synapse.account_service.service.TokenManagementService;
import com.synapse.account_service.util.AuthResponseWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenService jwtTokenService;
    private final TokenManagementService tokenManagementService;
    private final AuthResponseWriter authResponseWriter;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();

        String memberId = principalUser.providerUser() == null ? principalUser.member().getId().toString() : principalUser.providerUser().getId();

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new InternalAuthenticationServiceException("사용자에게 권한이 설정되어 있지 않습니다."));
        
        TokenResponse tokenResponse = jwtTokenService.createTokenResponse(memberId, role);

        tokenManagementService.saveOrUpdateRefreshToken(UUID.fromString(memberId), tokenResponse.refreshToken());

        authResponseWriter.writeSuccessResponse(response, tokenResponse);
    }
    
}
