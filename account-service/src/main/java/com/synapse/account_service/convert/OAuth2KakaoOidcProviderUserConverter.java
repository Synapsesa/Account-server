package com.synapse.account_service.convert;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.synapse.account_service.domain.ProviderUser;
import com.synapse.account_service.domain.enums.OAuth2Config;
import com.synapse.account_service.domain.socials.KakaoOidcUser;
import com.synapse.account_service.util.OAuth2Utils;

public final class OAuth2KakaoOidcProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {
    
    @Override
    public ProviderUser convert(ProviderUserRequest providerUserRequest) {

        if (!providerUserRequest.clientRegistration().getRegistrationId().equals(OAuth2Config.SocialType.KAKAO.getSocialName())) {
            return null;
        }

        if (!(providerUserRequest.oAuth2User() instanceof OidcUser)) {
            return null;
        }

        return new KakaoOidcUser(OAuth2Utils.getMainAttributes(
                providerUserRequest.oAuth2User()),
                providerUserRequest.oAuth2User(),
                providerUserRequest.clientRegistration());
    }
}
