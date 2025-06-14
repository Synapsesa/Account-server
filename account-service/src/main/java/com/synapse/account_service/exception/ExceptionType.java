package com.synapse.account_service.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExceptionType {
    DUPLICATED_EMAIL(CONFLICT, "001", "이미 존재하는 이메일입니다."),
    DUPLICATED_USERNAME(CONFLICT, "002", "이미 존재하는 사용자 이름입니다."),
    EXCEPTION(INTERNAL_SERVER_ERROR, "003", "예상치 못한 오류가 발생했습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
