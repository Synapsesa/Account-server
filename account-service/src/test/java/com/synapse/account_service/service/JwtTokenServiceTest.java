package com.synapse.account_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.synapse.account_service.dto.TokenResponse;
import com.synapse.account_service.dto.TokenResult;

@ExtendWith(MockitoExtension.class)
public class JwtTokenServiceTest {
    @InjectMocks
    private JwtTokenService jwtTokenService;

    @Mock
    private JwtTokenTemplate jwtTokenTemplate;

    @Test
    @DisplayName("토큰 생성 서비스 성공: 올바른 인자로 토큰 생성을 요청하고 DTO를 반환한다")
    void createTokenResponse_success() {
        // given
        ReflectionTestUtils.setField(jwtTokenService, "accessTokenExpirationMinutes", 30L);
        ReflectionTestUtils.setField(jwtTokenService, "refreshTokenExpirationMinutes", 1440L);

        UUID memberId = UUID.randomUUID();
        String role = "ROLE_USER";

        TokenResult mockAccessToken = new TokenResult("access.token.string", Instant.now().plusSeconds(1800));
        TokenResult mockRefreshToken = new TokenResult("refresh.token.string", Instant.now().plusSeconds(86400));

        // Access Token, Refresh Token이 순서대로 반환되도록 설정
        given(jwtTokenTemplate.createToken(anyString(), any(), anyLong())).willReturn(mockAccessToken, mockRefreshToken);

        // when
        TokenResponse tokenResponse = jwtTokenService.createTokenResponse(memberId.toString(), role);

        // then: 결과 검증
        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.accessToken().token()).isEqualTo("access.token.string");
        assertThat(tokenResponse.refreshToken().token()).isEqualTo("refresh.token.string");

        verify(jwtTokenTemplate, times(2)).createToken(anyString(), any(), anyLong());
    }
}
