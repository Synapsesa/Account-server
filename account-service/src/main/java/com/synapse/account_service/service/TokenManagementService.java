package com.synapse.account_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synapse.account_service.domain.Member;
import com.synapse.account_service.domain.RefreshToken;
import com.synapse.account_service.dto.TokenResult;
import com.synapse.account_service.dto.response.TokenResponse;
import com.synapse.account_service.exception.ExceptionType;
import com.synapse.account_service.exception.JWTValidationException;
import com.synapse.account_service.exception.NotFoundException;
import com.synapse.account_service.repository.MemberRepository;
import com.synapse.account_service.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenManagementService {
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    public void saveOrUpdateRefreshToken(UUID memberId, TokenResult refreshToken) {
        refreshTokenRepository.findById(memberId)
            .ifPresentOrElse(
                // 기존 토큰이 있으면, 새 토큰으로 값을 업데이트 (재로그인 시)
                existingToken -> existingToken.updateToken(refreshToken.token()),
                // 기존 토큰이 없으면, 새로 생성하여 저장 (최초 로그인)
                () -> {
                    RefreshToken newRefreshToken = new RefreshToken(memberId, refreshToken.token());
                    refreshTokenRepository.save(newRefreshToken);
                });
    }

    public TokenResponse reissueTokens(String requestRefreshToken) {
        UUID memberId = jwtTokenService.getMemberIdFrom(requestRefreshToken);

        RefreshToken storedToken = refreshTokenRepository.findById(memberId)
                .orElseThrow(() -> new JWTValidationException(ExceptionType.INVALID_REFRESH_TOKEN));

        if (!storedToken.getToken().equals(requestRefreshToken)) {
            refreshTokenRepository.delete(storedToken);
            throw new JWTValidationException(ExceptionType.TAMPERED_REFRESH_TOKEN);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.NOT_FOUND_MEMBER));
        
        String role = member.getRole().name();

        TokenResponse newTokens = jwtTokenService.createTokenResponse(memberId.toString(), role);

        storedToken.updateToken(newTokens.refreshToken().token());

        return newTokens;
    }
}
