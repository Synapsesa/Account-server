package com.synapse.account_service.domain.entity;

import java.util.UUID;

import com.synapse.account_service.domain.common.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token")
public class RefreshToken extends BaseTimeEntity {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID memberId;

    @Column(nullable = false, length = 512)
    private String token;

    @Builder
    public RefreshToken(UUID memberId, String token) {
        this.memberId = memberId;
        this.token = token;
    }

    public void updateToken(String newToken) {
        this.token = newToken;
    }
}
