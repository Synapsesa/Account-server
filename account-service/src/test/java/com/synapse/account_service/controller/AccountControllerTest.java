package com.synapse.account_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synapse.account_service.config.SecurityConfig;
import com.synapse.account_service.dto.request.SignUpRequest;
import com.synapse.account_service.dto.response.SignUpResponse;
import com.synapse.account_service.exception.ExceptionType;
import com.synapse.account_service.exception.GlobalExceptionHandler;
import com.synapse.account_service.exception.DuplicatedException;
import com.synapse.account_service.service.AccountService;
import com.synapse.account_service.service.CustomUserDetailsService;
import com.synapse.account_service.service.handler.LoginFailureHandler;
import com.synapse.account_service.service.handler.LoginSuccessHandler;

@WebMvcTest(AccountController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private LoginSuccessHandler loginSuccessHandler;

    @MockitoBean
    private LoginFailureHandler loginFailureHandler;

    @Test
    @DisplayName("회원가입 API 호출 성공")
    void signUpApi_success() throws Exception {
        // given
        UUID expectedId = UUID.randomUUID();
        SignUpRequest request = new SignUpRequest("test@example.com", "유저", "password1234");
        SignUpResponse response = new SignUpResponse(expectedId, "test@example.com", "유저", "USER");
        
        given(accountService.registerMember(any(SignUpRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/accounts/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(expectedId.toString()))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.username").value("유저"))
            .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("이메일 중복 시 409 Conflict 응답")
    void signUpApi_fail_withDuplicateEmail() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("test1@example.com", "유저", "password1234");
        
        given(accountService.registerMember(any(SignUpRequest.class)))
            .willThrow(new DuplicatedException(ExceptionType.DUPLICATED_EMAIL));
            
        // when & then
        mockMvc.perform(post("/api/accounts/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(ExceptionType.DUPLICATED_EMAIL.getCode()));
    }
    
    @Test
    @DisplayName("잘못된 요청값으로 회원가입 API 호출 시 400 Bad Request 응답")
    void signUpApi_fail_withInvalidInput() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("test.com", "password1234", "유저");

        // when & then
        mockMvc.perform(post("/api/accounts/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}
