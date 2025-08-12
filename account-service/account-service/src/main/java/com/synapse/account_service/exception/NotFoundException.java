package com.synapse.account_service.exception;

public class NotFoundException extends AccountServiceException {
    public NotFoundException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
