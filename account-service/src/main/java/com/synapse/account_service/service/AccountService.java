package com.synapse.account_service.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synapse.account_service.domain.Member;
import com.synapse.account_service.domain.enums.MemberRole;
import com.synapse.account_service.dto.SignUpRequest;
import com.synapse.account_service.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class AccountService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Member> findMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member registerMember(SignUpRequest request) {
        Member member = Member.builder()
                .email(request.email())
                // .password(encryptedPassword) // 암호화된 비밀번호 저장
                .username(request.username())
                .role(MemberRole.USER) // 기본 역할은 USER
                .provider("local") // 일반 회원가입이므로 "local"로 지정
                .build();
        
        findMemberByEmail(request.email()).ifPresent(m -> {
            // throw new UnauthorizedException(ExceptionType.DUPLICATED_EMAIL);
        });

        return member;
    }
}
