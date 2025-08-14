package com.synapse.account_service.service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synapse.account_service.domain.entity.Member;
import com.synapse.account_service.domain.entity.Subscription;
import com.synapse.account_service.domain.enums.SubscriptionTier;
import com.synapse.account_service.domain.repository.MemberRepository;
// import com.synapse.account_service.eventuate.publisher.MemberDomainEventPublisher;
import com.synapse.account_service.exception.DuplicatedException;
import com.synapse.account_service.exception.ExceptionType;
import com.synapse.account_service_api.dto.request.SignUpRequest;
import com.synapse.account_service_api.dto.response.SignUpResponse;
import com.synapse.account_service_api.event.MemberDomainEvent;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class AccountService {
    private final static String DEFAULT_PROVIDER = "default";
    private final static String DEFAULT_REGISTRATION_ID = "default";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    // private final MemberDomainEventPublisher memberDomainEventPublisher;

    @Async("signupTaskExecutor")
    @Transactional
    public CompletableFuture<SignUpResponse> registerMember(SignUpRequest request) {

        String encodedPassword = passwordEncoder.encode(request.password());
        
        memberRepository.findByUsernameAndEmail(request.username(), request.email()).ifPresent(m -> {
            throw new DuplicatedException(ExceptionType.DUPLICATED_USERNAME_AND_EMAIL);
        });

        ResultWithDomainEvents<Member, MemberDomainEvent> memberAndEvents = Member.register(
            UUID.randomUUID(),
            request.email(),
            request.username(),
            encodedPassword,
            DEFAULT_PROVIDER,
            DEFAULT_REGISTRATION_ID
        );

        Member memberResult = memberAndEvents.result;

        createAndSetDefaultSubscription(memberResult);

        memberRepository.save(memberResult);

        // memberDomainEventPublisher.publish(memberResult, memberAndEvents.events);

        return CompletableFuture.completedFuture(new SignUpResponse(
            memberResult.getId(), 
            memberResult.getEmail(),
            memberResult.getUsername(), 
            memberResult.getRole().name()
        ));
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
