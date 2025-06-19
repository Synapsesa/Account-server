package com.synapse.account_service.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExceptionType {
    DUPLICATED_EMAIL(CONFLICT, "001", "이미 존재하는 이메일입니다."),
    DUPLICATED_USERNAME(CONFLICT, "002", "이미 존재하는 사용자 이름입니다."),
    EXCEPTION(INTERNAL_SERVER_ERROR, "003", "예상치 못한 오류가 발생했습니다."),
    NOT_FOUND_MEMBER(NOT_FOUND, "004", "존재하지 않는 사용자입니다."),
    DUPLICATED_USERNAME_AND_EMAIL(CONFLICT, "005", "이미 존재하는 사용자 이름과 이메일입니다."),

    INVALID_TOKEN(UNAUTHORIZED, "005", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "006", "만료된 토큰입니다."),
    FAIL_LOGIN(UNAUTHORIZED, "007", "아이디 또는 비밀번호가 일치하지 않습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
