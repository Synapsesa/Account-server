package com.synapse.account_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.synapse.account_service.domain.Member;
import com.synapse.account_service.domain.RefreshToken;
import com.synapse.account_service.domain.enums.MemberRole;
import com.synapse.account_service.dto.TokenResult;
import com.synapse.account_service.dto.response.TokenResponse;
import com.synapse.account_service.exception.JWTValidationException;
import com.synapse.account_service.repository.MemberRepository;
import com.synapse.account_service.repository.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
public class TokenManagementServiceTest {
    @InjectMocks
    private TokenManagementService tokenManagementService;

    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private MemberRepository memberRepository;

    private UUID memberId;
    private String validRefreshToken;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        validRefreshToken = "valid.refresh.token";
    }

    @Test
    @DisplayName("토큰 재발급 성공: 유효한 리프레시 토큰으로 요청 시, 새로운 토큰 쌍을 반환하고 DB를 갱신한다")
    void reissueTokens_success() {
        // given
        RefreshToken storedToken = new RefreshToken(memberId, validRefreshToken);
        Member member = Member.builder()
            .role(MemberRole.USER)
            .build();
        TokenResponse newTokens = new TokenResponse(
            new TokenResult("new.access.token", Instant.now().plusSeconds(1800)),
            new TokenResult("new.refresh.token", Instant.now().plusSeconds(86400))
        );

        given(jwtTokenService.getMemberIdFrom(validRefreshToken)).willReturn(memberId);
        given(refreshTokenRepository.findById(memberId)).willReturn(Optional.of(storedToken));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(jwtTokenService.createTokenResponse(memberId.toString(), "USER")).willReturn(newTokens);

        // when
        TokenResponse result = tokenManagementService.reissueTokens(validRefreshToken);

        // then
        assertThat(result.accessToken().token()).isEqualTo("new.access.token");
        assertThat(storedToken.getToken()).isEqualTo("new.refresh.token"); // Rotation 검증
        verify(refreshTokenRepository).findById(memberId);
        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("토큰 재발급 실패: DB에 저장된 토큰과 일치하지 않으면 InvalidTokenException을 던지고 DB에서 삭제한다 (탈취 의심)")
    void reissueTokens_fail_whenTokenMismatched() {
        // given
        RefreshToken storedToken = new RefreshToken(memberId, "different.token.in.db");

        given(jwtTokenService.getMemberIdFrom(validRefreshToken)).willReturn(memberId);
        given(refreshTokenRepository.findById(memberId)).willReturn(Optional.of(storedToken));

        // when & then
        assertThrows(JWTValidationException.class, () -> {
            tokenManagementService.reissueTokens(validRefreshToken);
        });

        // 탈취 시도로 간주하고, DB에서 해당 토큰을 삭제했는지 검증
        verify(refreshTokenRepository).delete(storedToken);
    }

    @Test
    @DisplayName("토큰 재발급 실패: DB에 리프레시 토큰이 없으면 InvalidTokenException을 던진다")
    void reissueTokens_fail_whenTokenNotFoundInDb() {
        // given
        given(jwtTokenService.getMemberIdFrom(validRefreshToken)).willReturn(memberId);
        given(refreshTokenRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThrows(JWTValidationException.class, () -> {
            tokenManagementService.reissueTokens(validRefreshToken);
        });
    }
}
