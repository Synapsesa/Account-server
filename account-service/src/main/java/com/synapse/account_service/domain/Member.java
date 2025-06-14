package com.synapse.account_service.domain;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.synapse.account_service.common.BaseEntity;
import com.synapse.account_service.domain.enums.MemberRole;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "provider", length = 10)
    private String provider;

    @Column(name = "picture", columnDefinition = "TEXT")
    private String picture;

    @Column(name = "registration_id")
    private String registrationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Subscription subscription;

    @Builder
    public Member(String username, String password, String email, String provider, String picture, String registrationId, MemberRole role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.provider = provider;
        this.picture = picture;
        this.registrationId = registrationId;
        this.role = role;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
        if (subscription != null) {
            subscription.setMemberInternal(this); // 무한 루프 방지를 위해 내부 메서드 호출
        }
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}
