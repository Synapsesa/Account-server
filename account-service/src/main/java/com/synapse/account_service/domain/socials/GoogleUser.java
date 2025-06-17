package com.synapse.account_service.domain.socials;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.synapse.account_service.domain.ProviderUser;

public class GoogleUser implements ProviderUser {

    private Map<String, Object> attributes;
    private OAuth2User oAuth2User;
    private ClientRegistration clientRegistration;

    public GoogleUser(Map<String, Object> attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        this.attributes = attributes;
        this.oAuth2User = oAuth2User;
        this.clientRegistration = clientRegistration;
    }

    @Override
    public String getId() {
        return (String) getAttributes().get("sub");
    }

    @Override
    public String getUsername() {
        return (String) getAttributes().get("name");
    }

    @Override
    public String getPicture() {
        return null;
    }

    @Override
    public String getPassword() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getProvider() {
        return clientRegistration.getRegistrationId();
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority())).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public OAuth2User getOAuth2User() {
        return this.oAuth2User;
    }

    @Override
    public OidcIdToken getIdToken() {
        if(oAuth2User instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) oAuth2User;
            return oidcUser.getIdToken();
        }
        return null;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        if(oAuth2User instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) oAuth2User;
            return oidcUser.getUserInfo();
        }
        return null;
    }

}