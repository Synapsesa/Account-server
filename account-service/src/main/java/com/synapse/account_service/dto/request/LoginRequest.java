package com.synapse.account_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "사용자 이름은 필수 입력 항목입니다.") 
    String username,

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.") 
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.") 
    String password
) {
    
}
