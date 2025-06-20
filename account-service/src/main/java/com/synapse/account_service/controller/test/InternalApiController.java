package com.synapse.account_service.controller.test;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 내부 api를 테스트하는 테스트 api controller 입니다.
 */
@RestController
@RequestMapping("/api/internal")
public class InternalApiController {
    
    @GetMapping("/accounts/id")
    public ResponseEntity<String> getAccountInfo(Authentication authentication) {
        // 인증된 주체(클라이언트 ID)와 요청된 ID를 로깅합니다.
        System.out.println("Client '" + authentication.getName() + "' requested info for account: ");
        return ResponseEntity.ok("Account info for ");
    }
}
