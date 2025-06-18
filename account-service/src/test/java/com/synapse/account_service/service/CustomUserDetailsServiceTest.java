package com.synapse.account_service.service;

import com.synapse.account_service.convert.ProviderUserConverter;
import com.synapse.account_service.convert.ProviderUserRequest;
import com.synapse.account_service.domain.Member;
import com.synapse.account_service.domain.PrincipalUser;
import com.synapse.account_service.domain.ProviderUser;
import com.synapse.account_service.domain.enums.MemberRole;
import com.synapse.account_service.domain.forms.FormUser;
import com.synapse.account_service.exception.NotFoundException;
import com.synapse.account_service.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter;

    @BeforeEach
    void setUp() {
        customUserDetailsService = new CustomUserDetailsService();
        ReflectionTestUtils.setField(customUserDetailsService, "memberRepository", memberRepository);
        ReflectionTestUtils.setField(customUserDetailsService, "providerUserConverter", providerUserConverter);
    }
    
    @Test
    @DisplayName("사용자 조회 성공: DB에 존재하는 사용자를 PrincipalUser 객체로 변환하여 반환한다")
    void loadUserByUsername_whenUserExists_returnsPrincipalUser() {
        // given
        String username = "testuser";
        String email = "test@test.com";
        Member mockMember = Member.builder()
                .id(UUID.randomUUID())
                .username(username)
                .email(email)
                .password("password")
                .role(MemberRole.USER)
                .build();
        
        ProviderUser mockProviderUser = FormUser.builder()
                .id(mockMember.getId())
                .username(mockMember.getUsername())
                .password(mockMember.getPassword())
                .email(mockMember.getEmail())
                .authorities(mockMember.getRole().getAuthorities())
                .build();

        given(memberRepository.findByUsername(username)).willReturn(Optional.of(mockMember));
        given(providerUserConverter.convert(any(ProviderUserRequest.class))).willReturn(mockProviderUser);

        // when
        PrincipalUser principalUser = (PrincipalUser) customUserDetailsService.loadUserByUsername(username);

        // then
        assertThat(principalUser).isNotNull();
        assertThat(principalUser.getUsername()).isEqualTo(username);
        assertThat(principalUser.providerUser().getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("사용자 조회 실패: DB에 사용자가 없으면 NotFoundException을 던진다")
    void loadUserByUsername_whenUserNotExists_throwsException() {
        // given
        String username = "nonexistent";
        given(memberRepository.findByUsername(username)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(username))
                .isInstanceOf(NotFoundException.class);
    }
}
