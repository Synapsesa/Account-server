package com.synapse.account_service.service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synapse.account_service.domain.Member;
import com.synapse.account_service.domain.Subscription;
import com.synapse.account_service.domain.enums.MemberRole;
import com.synapse.account_service.domain.enums.SubscriptionTier;
import com.synapse.account_service.dto.request.SignUpRequest;
import com.synapse.account_service.dto.response.SignUpResponse;
import com.synapse.account_service.exception.ExceptionType;
import com.synapse.account_service.exception.DuplicatedException;
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

    public Optional<Member> findMemberByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public SignUpResponse registerMember(SignUpRequest request) {
        findMemberByEmail(request.email()).ifPresent(m -> {
            throw new DuplicatedException(ExceptionType.DUPLICATED_EMAIL);
        });

        findMemberByUsername(request.username()).ifPresent(m -> {
            throw new DuplicatedException(ExceptionType.DUPLICATED_USERNAME);
        });

        Member member = Member.builder()
                .email(request.email())
                .password(request.password())
                .username(request.username())
                .role(MemberRole.USER) // 기본 역할은 USER
                .provider("local") // 일반 회원가입이므로 "local"로 지정
                .build();
        
        member.encodePassword(passwordEncoder);

        createAndSetDefaultSubscription(member);

        memberRepository.save(member);

        return SignUpResponse.from(member);
    }

    private void createAndSetDefaultSubscription(Member member) {
        ZonedDateTime nextRenewalDate = ZonedDateTime.now(ZoneId.systemDefault()).plusDays(1).with(LocalTime.MIDNIGHT); // 무료 사용자는 자정 초기화

        Subscription freeSubscription = Subscription.builder()
                .tier(SubscriptionTier.FREE)
                .nextRenewalDate(nextRenewalDate)
                .build();
        
        member.setSubscription(freeSubscription);
    }
}
