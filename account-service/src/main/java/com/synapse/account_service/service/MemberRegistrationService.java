package com.synapse.account_service.service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synapse.account_service.domain.Member;
import com.synapse.account_service.domain.ProviderUser;
import com.synapse.account_service.domain.Subscription;
import com.synapse.account_service.domain.enums.MemberRole;
import com.synapse.account_service.domain.enums.SubscriptionTier;
import com.synapse.account_service.exception.DuplicatedException;
import com.synapse.account_service.exception.ExceptionType;
import com.synapse.account_service.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberRegistrationService {

    private final MemberRepository memberRepository;

    public Optional<Member> findByProviderAndRegistrationId(String provider, String registrationId) {
        return memberRepository.findByProviderAndRegistrationId(provider, registrationId);
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public Member registerOauthUser(ProviderUser providerUser) {
        return createAndSaveNewMember(
            providerUser.getEmail(),
            providerUser.getUsername(),
            providerUser.getPassword(),
            providerUser.getProvider(),
            providerUser.getId()
        );
    }

    private Member createAndSaveNewMember(String email, String username, String password, String provider, String registrationId) {
        // 중복 검사
        memberRepository.findByEmail(email).ifPresent(m -> {
            throw new DuplicatedException(ExceptionType.DUPLICATED_EMAIL);
        });

        memberRepository.findByUsername(username).ifPresent(m -> {
            throw new DuplicatedException(ExceptionType.DUPLICATED_USERNAME);
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
