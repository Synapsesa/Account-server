package com.synapse.account_service.convert;

import com.synapse.account_service.domain.Member;
import com.synapse.account_service.domain.ProviderUser;
import com.synapse.account_service.domain.forms.FormUser;

public final class UserDetailsProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {

    @Override
    public ProviderUser convert(ProviderUserRequest providerUserRequest) {

        Member member = providerUserRequest.member();
        
        return FormUser.builder()
            .id(member.getId())
            .username(member.getUsername())
            .password(member.getPassword())
            .email(member.getEmail())
            .provider(member.getProvider())
            .authorities(member.getRole().getAuthorities())
            .build();
    }
}
