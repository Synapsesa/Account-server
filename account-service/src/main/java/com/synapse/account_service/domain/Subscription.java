package com.synapse.account_service.domain;

import java.time.ZonedDateTime;

import com.synapse.account_service.common.BaseTimeEntity;
import com.synapse.account_service.domain.enums.SubscriptionTier;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "subscription")
public class Subscription extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "tier", nullable = false)
    private SubscriptionTier tier = SubscriptionTier.FREE;

    @Column(name = "next_renewal_date", nullable = false)
    private ZonedDateTime nextRenewalDate;

    @Builder
    public Subscription(Member member, SubscriptionTier tier, ZonedDateTime nextRenewalDate) {
        this.member = member;
        this.tier = tier;
        this.nextRenewalDate = nextRenewalDate;
    }

    protected void setMemberInternal(Member member) {
        this.member = member;
    }
}
