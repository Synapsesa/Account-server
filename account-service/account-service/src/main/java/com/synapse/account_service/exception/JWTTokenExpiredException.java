package com.synapse.account_service.exception;

public class JWTTokenExpiredException extends AccountServiceException {
    public JWTTokenExpiredException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
