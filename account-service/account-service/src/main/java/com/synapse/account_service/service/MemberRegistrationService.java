package com.synapse.account_service.service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synapse.account_service.domain.ProviderUser;
import com.synapse.account_service.domain.entity.Member;
import com.synapse.account_service.domain.entity.Subscription;
import com.synapse.account_service.domain.enums.SubscriptionTier;
import com.synapse.account_service.domain.repository.MemberRepository;
// import com.synapse.account_service.eventuate.publisher.MemberDomainEventPublisher;
import com.synapse.account_service_api.event.MemberDomainEvent;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberRegistrationService {

    private final MemberRepository memberRepository;
    // private final MemberDomainEventPublisher memberDomainEventPublisher;

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

        ResultWithDomainEvents<Member, MemberDomainEvent> memberAndEvents = Member.register(
            UUID.randomUUID(),
            providerUser.getEmail(),
            providerUser.getUsername(),
            providerUser.getPassword(),
            provider,
            providerUser.getId()
        );

        Member memberResult = memberAndEvents.result;

        createAndSetDefaultSubscription(memberResult);

        memberRepository.save(memberResult);

        // memberDomainEventPublisher.publish(memberResult, memberAndEvents.events);

        return memberResult;
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
