package com.synapse.account_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.synapse.account_service.domain.Member;
import com.synapse.account_service.domain.PrincipalUser;
import com.synapse.account_service.domain.enums.MemberRole;
import com.synapse.account_service.exception.NotFoundException;
import com.synapse.account_service.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("사용자 조회 성공: DB에 존재하는 사용자를 PrincipalUser 객체로 변환하여 반환한다")
    void loadUserByUsername_success() {
        // given
        String username = "test@example.com";
        Member mockMember = Member.builder()
                .id(UUID.randomUUID())
                .username(username)
                .password("encodedPassword")
                .role(MemberRole.USER)
                .build();

        given(memberRepository.findByUsername(username)).willReturn(Optional.of(mockMember));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // then
        assertThat(userDetails).isInstanceOf(PrincipalUser.class);
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("USER");
    }

    @Test
    @DisplayName("사용자 조회 실패: DB에 사용자가 없으면 UsernameNotFoundException을 던진다")
    void loadUserByUsername_fail_userNotFound() {
        // given
        String username = "notfound@example.com";
        given(memberRepository.findByUsername(username)).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });
    }
}
