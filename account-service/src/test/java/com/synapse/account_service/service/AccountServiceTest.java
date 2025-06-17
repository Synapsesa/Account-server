package com.synapse.account_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.synapse.account_service.domain.Member;
import com.synapse.account_service.dto.request.SignUpRequest;
import com.synapse.account_service.dto.response.SignUpResponse;
import com.synapse.account_service.exception.DuplicatedException;
import com.synapse.account_service.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks 
    private AccountService accountService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공")
    void signUp_success() {
        // given: 테스트 준비
        SignUpRequest request = new SignUpRequest("test@example.com", "테스트유저", "password123");
        
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(memberRepository.findByUsername(anyString())).willReturn(Optional.empty());
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> {
            Member memberToSave = invocation.getArgument(0);
            return memberToSave;
        });
        
        // when: 실제 테스트할 메서드 호출
        SignUpResponse response = accountService.registerMember(request);
        
        // then: 결과 검증
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.username()).isEqualTo("테스트유저");
        
        verify(passwordEncoder).encode("password123");
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("이메일 중복으로 회원가입 실패")
    void signUp_fail_withDuplicateEmail() {
        // given
        SignUpRequest request = new SignUpRequest("test@example.com", "테스트유저", "password123");
        
        // memberRepository.findByEmail이 호출되면, 이미 존재하는 Member 객체를 반환하도록 설정
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(Member.builder().build()));
        
        // when & then: BusinessException이 발생하는지 검증
        assertThrows(DuplicatedException.class, () -> {
            accountService.registerMember(request);
        });
    }
}
