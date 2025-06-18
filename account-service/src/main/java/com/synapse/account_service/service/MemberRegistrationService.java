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
import com.synapse.account_service.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberRegistrationService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member registerOauthUser(String provider, ProviderUser providerUser) {
        Optional<Member> memberOptional = memberRepository.findBySocialIdOrEmailOrUsername(
            provider, providerUser.getId(), providerUser.getEmail(), providerUser.getUsername()
        );

        if (memberOptional.isPresent()) {
            Member existingMember = memberOptional.get();
            if (existingMember.getProvider() == null || existingMember.getRegistrationId() == null) {
                existingMember.linkSocialAccount(provider, providerUser.getId());
            }
            return existingMember;
        }

        // 신규 회원 생성
        return createAndSaveNewMember(
            providerUser.getEmail(),
            providerUser.getUsername(),
            providerUser.getPassword(),
            provider,
            providerUser.getId()
        );
    }

    private Member createAndSaveNewMember(String email, String username, String password, String provider, String registrationId) {
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
