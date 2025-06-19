package com.synapse.account_service.service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synapse.account_service.domain.Member;
import com.synapse.account_service.domain.Subscription;
import com.synapse.account_service.domain.enums.MemberRole;
import com.synapse.account_service.domain.enums.SubscriptionTier;
import com.synapse.account_service.dto.request.SignUpRequest;
import com.synapse.account_service.dto.response.SignUpResponse;
import com.synapse.account_service.exception.DuplicatedException;
import com.synapse.account_service.exception.ExceptionType;
import com.synapse.account_service.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class AccountService {
    private final static String DEFAULT_PROVIDER = "default";
    private final static String DEFAULT_REGISTRATION_ID = "default";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignUpResponse registerMember(SignUpRequest request) {

        String encodedPassword = passwordEncoder.encode(request.password());
        
        Member savedMember = createAndSaveNewMember(
            request.email(),
            request.username(),
            encodedPassword,
            DEFAULT_PROVIDER,
            DEFAULT_REGISTRATION_ID
        );

        return SignUpResponse.from(savedMember);
    }

    private Member createAndSaveNewMember(String email, String username, String password, String provider, String registrationId) {
        // 중복 검사
        memberRepository.findByUsernameAndEmail(username, email).ifPresent(m -> {
            throw new DuplicatedException(ExceptionType.DUPLICATED_USERNAME_AND_EMAIL);
        });

        Member member = Member.builder()
                .email(email)
                .password(password)
                .username(username)
                .role(MemberRole.USER)
                .provider(provider)
                .registrationId(registrationId)
                .build();
        
        createAndSetDefaultSubscription(member); // 기본 구독 설정
        return memberRepository.save(member);
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
