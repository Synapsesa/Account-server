package com.synapse.account_service.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.synapse.account_service.service.AccountService;
import com.synapse.account_service_api.dto.request.SignUpRequest;
import com.synapse.account_service_api.dto.response.SignUpResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/signup")
    public CompletableFuture<ResponseEntity<SignUpResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        CompletableFuture<SignUpResponse> futureResponse = accountService.registerMember(request);

        return futureResponse
                .thenApply(
                    responseBody -> ResponseEntity.status(CREATED).body(responseBody)
                )
                .exceptionally(ex -> {
                    return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
                });
    }
}
