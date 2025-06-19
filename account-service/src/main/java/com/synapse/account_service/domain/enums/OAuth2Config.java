package com.synapse.account_service.domain.enums;

public class OAuth2Config {
    public enum SocialType {
        GOOGLE("google"),
        KAKAO("kakao"),
        NAVER("naver")
        ;

        private final String socialName;

        private SocialType(String socialName) {
            this.socialName = socialName;
        }

        public String getSocialName() {
            return socialName;
        }
    }
}
