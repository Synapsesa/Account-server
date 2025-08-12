package com.synapse.account_service.exception;

public class JWTValidationException extends AccountServiceException {
    public JWTValidationException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
