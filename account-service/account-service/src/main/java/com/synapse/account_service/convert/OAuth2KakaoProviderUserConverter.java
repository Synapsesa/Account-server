package com.synapse.account_service.convert;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.synapse.account_service.domain.ProviderUser;
import com.synapse.account_service.domain.enums.OAuth2Type;
import com.synapse.account_service.domain.socials.KakaoUser;
import com.synapse.account_service.utils.OAuth2Utils;

public final class OAuth2KakaoProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {
    
    @Override
    public ProviderUser convert(ProviderUserRequest providerUserRequest) {

        if (!providerUserRequest.clientRegistration().getRegistrationId().equals(OAuth2Type.KAKAO.getSocialName())) {
            return null;
        }

        if (providerUserRequest.oAuth2User() instanceof OidcUser) {
            return null;
        }

        return new KakaoUser(OAuth2Utils.getOtherAttributes(
                providerUserRequest.oAuth2User(), "kakao_account", "profile"),
                providerUserRequest.oAuth2User(),
                providerUserRequest.clientRegistration());
    }
}
