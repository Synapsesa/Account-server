package com.synapse.account_service.exception.dto;

import com.synapse.account_service.exception.AccountServiceException;
import com.synapse.account_service.exception.ExceptionType;

public record ExceptionResponse(
    String code,
    String message
) {
    public static ExceptionResponse from(AccountServiceException exception) {
        return ExceptionResponse.from(exception.getExceptionType());
    }

    public static ExceptionResponse from(ExceptionType exceptionType) {
        return new ExceptionResponse(exceptionType.getCode(), exceptionType.getMessage());
    }
}
