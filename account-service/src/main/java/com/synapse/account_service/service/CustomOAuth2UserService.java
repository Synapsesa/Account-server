package com.synapse.account_service.service;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.synapse.account_service.convert.ProviderUserRequest;
import com.synapse.account_service.domain.Member;
import com.synapse.account_service.domain.PrincipalUser;
import com.synapse.account_service.domain.ProviderUser;

@Service
public class CustomOAuth2UserService extends AbstractOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

    public CustomOAuth2UserService() {
        this.oAuth2UserService = new DefaultOAuth2UserService();
    }

    // 테스트용 생성자
    public CustomOAuth2UserService(OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService) {
        this.oAuth2UserService = oAuth2UserService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        ProviderUserRequest providerUserRequest = new ProviderUserRequest(clientRegistration, oAuth2User);
        ProviderUser providerUser = providerUser(providerUserRequest);

        Member member = super.register(providerUser, userRequest);

        return new PrincipalUser(providerUser, member);
    }
}
