package com.synapse.account_service.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.synapse.account_service.convert.ProviderUserRequest;
import com.synapse.account_service.domain.Member;
import com.synapse.account_service.domain.PrincipalUser;
import com.synapse.account_service.domain.ProviderUser;
import com.synapse.account_service.domain.forms.FormUser;
import com.synapse.account_service.exception.ExceptionType;
import com.synapse.account_service.exception.NotFoundException;
import com.synapse.account_service.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(ExceptionType.NOT_FOUND_MEMBER));

        ProviderUserRequest providerUserRequest = new ProviderUserRequest(member);
        ProviderUser providerUser = providerUser(providerUserRequest);

        return new PrincipalUser(providerUser);
    }

    private ProviderUser providerUser(ProviderUserRequest providerUserRequest) {
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
