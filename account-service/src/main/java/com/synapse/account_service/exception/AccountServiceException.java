package com.synapse.account_service.exception;

import lombok.Getter;

@Getter
public abstract class AccountServiceException extends RuntimeException {
    private final ExceptionType exceptionType;

    protected AccountServiceException(final ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
