package com.synapse.account_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Service;

import com.synapse.account_service.convert.ProviderUserConverter;
import com.synapse.account_service.convert.ProviderUserRequest;
import com.synapse.account_service.domain.Member;
import com.synapse.account_service.domain.ProviderUser;
import com.synapse.account_service.repository.MemberRepository;

@Service
public abstract class AbstractOAuth2UserService {

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    private MemberRegistrationService memberRegistrationService;

    @Autowired
    private ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter;

    public Member register(ProviderUser providerUser, OAuth2UserRequest userRequest) {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        return memberRegistrationService.registerOauthUser(clientRegistration.getRegistrationId(), providerUser);
    }

    public ProviderUser providerUser(ProviderUserRequest providerUserRequest) {
        return providerUserConverter.convert(providerUserRequest);
    }
}
