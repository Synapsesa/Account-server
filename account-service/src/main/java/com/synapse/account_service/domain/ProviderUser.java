package com.synapse.account_service.domain;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface ProviderUser {
    UUID getId();

    String getUsername();

    String getPassword();

    String getEmail();

    String getProvider();

    String getPicture();

    List<? extends GrantedAuthority> getAuthorities();

    Map<String, Object> getAttributes();

    OAuth2User getOAuth2User();
}
