package com.synapse.account_service.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.synapse.account_service.dto.RefreshTokenResponse;
import com.synapse.account_service.dto.response.TokenResponse;
import com.synapse.account_service.service.TokenManagementService;
import com.synapse.account_service.util.AuthResponseWriter;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts/token")
@RequiredArgsConstructor
public class TokenReissueController {
    private final TokenManagementService tokenManagementService;
    private final AuthResponseWriter authResponseWriter;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue(name = "refreshToken") String refreshToken) {
        TokenResponse newTokens = tokenManagementService.reissueTokens(refreshToken);

        RefreshTokenResponse response = authResponseWriter.writeSuccessResponse(newTokens);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, response.cookie().toString()).body(response.responseBody());
    }
}
