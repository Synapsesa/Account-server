package com.synapse.account_service.domain;

import java.util.UUID;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    private UUID memberId;
    private String token;

    @Builder
    public RefreshToken(UUID memberId, String token) {
        this.memberId = memberId;
        this.token = token;
    }
}
