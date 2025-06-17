package com.synapse.account_service.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.synapse.account_service.domain.Member;
import com.synapse.account_service.domain.PrincipalUser;
import com.synapse.account_service.domain.ProviderUser;

@ExtendWith(MockitoExtension.class)
public class CustomOAuth2UserServiceTest {
    
    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @Mock
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

    @Mock
    private MemberRegistrationService registrationService;

    private OAuth2UserRequest googleUserRequest;
    private OAuth2User mockGoogleUser;
    private String userEmail = "google_user@example.com";
    private String providerId = "1234567890";

    @BeforeEach
    void setUp() {
        // 테스트 대상 서비스 수동 생성 및 의존성 주입
        customOAuth2UserService = new CustomOAuth2UserService(oAuth2UserService, registrationService);

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("google")
                .clientId("test-id")
                .clientSecret("test-secret")
                .userNameAttributeName("sub")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .build();
        
        Map<String, Object> attributes = Map.of(
            "sub", providerId,
            "name", "구글유저",
            "email", userEmail
        );
        
        mockGoogleUser = new DefaultOAuth2User(Collections.emptyList(), attributes, "sub");
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "test-token", Instant.now(), Instant.now().plusSeconds(60));
        googleUserRequest = new OAuth2UserRequest(clientRegistration, accessToken);
    }

    @Test
    @DisplayName("시나리오 1: 신규 소셜 사용자일 경우, 회원가입 로직이 호출된다")
    void loadUser_whenNewUser_shouldRegisterMember() {
        // given
        given(oAuth2UserService.loadUser(any(OAuth2UserRequest.class))).willReturn(mockGoogleUser);
        given(registrationService.findByProviderAndRegistrationId(anyString(), anyString())).willReturn(Optional.empty());
        given(registrationService.findByEmail(anyString())).willReturn(Optional.empty());

        // 1차, 2차 조회 모두 실패하여 사용자가 없다고 가정
        Member newMember = Member.builder().email(userEmail).username("구글유저").build();
        given(registrationService.registerOauthUser(any(ProviderUser.class))).willReturn(newMember);

        // when
        OAuth2User result = customOAuth2UserService.loadUser(googleUserRequest);
        
        verify(registrationService, times(1)).registerOauthUser(any(ProviderUser.class));

        assertThat(result).isInstanceOf(PrincipalUser.class);
        assertThat(((PrincipalUser) result).member()).isNotNull();
        assertThat(((PrincipalUser) result).member().getEmail()).isEqualTo(userEmail);
    }
    
    @Test
    @DisplayName("시나리오 2: 이미 동일한 소셜 계정으로 가입한 경우, 회원가입 없이 로그인 처리된다")
    void loadUser_whenExistingSocialUser_shouldNotRegister() {
        // given
        given(oAuth2UserService.loadUser(any(OAuth2UserRequest.class))).willReturn(mockGoogleUser);
        
        Member existingMember = Member.builder().email(userEmail).build();
        // 1차 조회에서 사용자를 찾았다고 가정
        given(registrationService.findByProviderAndRegistrationId("google", providerId)).willReturn(Optional.of(existingMember));

        // when
        OAuth2User result = customOAuth2UserService.loadUser(googleUserRequest);

        // then
        assertThat(result).isInstanceOf(PrincipalUser.class);
        assertThat(((PrincipalUser) result).member().getEmail()).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("시나리오 3: 이미 동일한 이메일의 일반 계정이 있을 경우, 계정이 연동된다")
    void loadUser_whenExistingLocalUser_shouldLinkAccount() {
        // given
        given(oAuth2UserService.loadUser(any(OAuth2UserRequest.class))).willReturn(mockGoogleUser);
        
        Member existingMember = mock(Member.class); 
        
        given(registrationService.findByProviderAndRegistrationId(anyString(), anyString())).willReturn(Optional.empty());
        given(registrationService.findByEmail(userEmail)).willReturn(Optional.of(existingMember));

        // when
        OAuth2User result = customOAuth2UserService.loadUser(googleUserRequest);

        // then
        verify(existingMember, times(1)).linkSocialAccount("google", providerId);

        assertThat(result).isInstanceOf(PrincipalUser.class);
    }
}
