package com.synapse.account_service.dto.response;

import com.synapse.account_service.domain.Member;

public record SignUpResponse(
    Long id,
    String email,
    String username,
    String role
) {
    public static SignUpResponse from(Member member) {
        return new SignUpResponse(member.getId(), member.getEmail(), member.getUsername(), member.getRole().name());
    }
}
