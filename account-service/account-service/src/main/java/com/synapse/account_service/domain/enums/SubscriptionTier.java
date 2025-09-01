package com.synapse.account_service.domain.enums;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SubscriptionTier {
    FREE("FREE", 0, 3),
    PRO("PRO", 0, 100)
    ;

    private final String SubscriptionTierName;
    private final int currentSubscriptionCount;
    private final int maxSubscriptionCount;

    private SubscriptionTier(String SubscriptionTierName, int currentSubscriptionCount, int maxSubscriptionCount) {
        this.SubscriptionTierName = SubscriptionTierName;
        this.currentSubscriptionCount = currentSubscriptionCount;
        this.maxSubscriptionCount = maxSubscriptionCount;
    }

    public String getSubscriptionTierName() {
        return SubscriptionTierName;
    }

    public int getCurrentSubscriptionCount() {
        return currentSubscriptionCount;
    }

    public int getMaxSubscriptionCount() {
        return maxSubscriptionCount;
    }

    public static final Map<String, SubscriptionTier> TIER_MAP = Collections.unmodifiableMap(
        Stream.of(values())
            .collect(Collectors.toMap(SubscriptionTier::getSubscriptionTierName, Function.identity()))
    );

    public static SubscriptionTier fromTier(String tierName) {
        SubscriptionTier result = TIER_MAP.get(tierName);
        if(result == null) {
            throw new IllegalArgumentException("일치하는 티어 타입이 없습니다." + tierName);
        }
        return result;
    }
}
