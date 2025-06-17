package com.synapse.account_service.service;

import java.util.Optional;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.synapse.account_service.convert.ProviderUserRequest;
import com.synapse.account_service.domain.Member;
import com.synapse.account_service.domain.PrincipalUser;
import com.synapse.account_service.domain.ProviderUser;
import com.synapse.account_service.domain.socials.GoogleUser;
import com.synapse.account_service.repository.MemberRepository;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;
    private final MemberRegistrationService registrationService;

    @Autowired
    public CustomOAuth2UserService(MemberRepository memberRepository, MemberRegistrationService registrationService) {
        this.oAuth2UserService = new DefaultOAuth2UserService();
        this.registrationService = registrationService;
    }

    // 테스트용 생성자
    public CustomOAuth2UserService(OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService, MemberRegistrationService registrationService) {
        this.oAuth2UserService = oAuth2UserService;
        this.registrationService = registrationService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        ProviderUserRequest providerUserRequest = new ProviderUserRequest(clientRegistration, oAuth2User);
        ProviderUser providerUser = providerUser(providerUserRequest);

        Member member = findOrRegisterMember(providerUser, userRequest);

        return new PrincipalUser(providerUser, member);
    }

    private Member findOrRegisterMember(ProviderUser providerUser, OAuth2UserRequest userRequest) {
        Optional<Member> memberOptional = registrationService.findByProviderAndRegistrationId(
            providerUser.getProvider(),
            providerUser.getId()
        );

        if (memberOptional.isPresent()) {
            return memberOptional.get();
        }

        if(providerUser.getEmail() != null) {
            Optional<Member> memberByEmailOpt = registrationService.findByEmail(providerUser.getEmail());
            if(memberByEmailOpt.isPresent()) {
                Member existingMember = memberByEmailOpt.get();
                existingMember.linkSocialAccount(providerUser.getProvider(), providerUser.getId());
                return existingMember;
            }
        }

        return registrationService.registerOauthUser(providerUser);
    }

    private ProviderUser providerUser(ProviderUserRequest providerUserRequest) {
        if (!providerUserRequest.clientRegistration().getRegistrationId().equals("google")) {
            return null;
        }

        return new GoogleUser(
            providerUserRequest.oAuth2User().getAttributes(),
            providerUserRequest.oAuth2User(),
            providerUserRequest.clientRegistration()
        );
    }
    
}
