package com.synapse.account_service.domain.enums;

public enum OAuth2Type {
    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver")
    ;

    private final String socialName;

    private OAuth2Type(String socialName) {
        this.socialName = socialName;
    }

    public String getSocialName() {
        return socialName;
    }
}
