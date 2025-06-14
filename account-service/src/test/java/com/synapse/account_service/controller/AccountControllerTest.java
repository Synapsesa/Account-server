package com.synapse.account_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@WebMvcTest(AccountController.class) // AccountController만 테스트
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @Test
    @DisplayName("회원가입 API 호출 성공")
    void signUpApi_success() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("test@example.com", "유저", "password1234");
        SignUpResponse response = new SignUpResponse(1L, "test@example.com", "유저", "USER");
        
        given(accountService.registerMember(any(SignUpRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/accounts/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated()) // 201 Created 상태인지 확인
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.username").value("유저"))
            .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("이메일 중복 시 409 Conflict 응답")
    void signUpApi_fail_withDuplicateEmail() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("test1@example.com", "유저", "password1234");
        
        // accountService.registerMember가 호출되면 BusinessException을 던지도록 설정
        given(accountService.registerMember(any(SignUpRequest.class)))
            .willThrow(new DuplicatedException(ExceptionType.DUPLICATED_EMAIL));
            
        // when & then
        mockMvc.perform(post("/api/accounts/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict()) // 409 Conflict 상태인지 확인
            .andExpect(jsonPath("$.code").value(ExceptionType.DUPLICATED_EMAIL.getCode()));
    }
    
    @Test
    @DisplayName("잘못된 요청값으로 회원가입 API 호출 시 400 Bad Request 응답")
    void signUpApi_fail_withInvalidInput() throws Exception {
        // given
        // 이메일 형식이 잘못된 요청
        SignUpRequest request = new SignUpRequest("test.com", "password1234", "유저");

        // when & then
        mockMvc.perform(post("/api/accounts/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest()); // @Valid에 의해 400 Bad Request가 발생하는지 확인
    }
}
