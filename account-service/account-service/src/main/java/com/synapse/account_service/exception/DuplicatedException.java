package com.synapse.account_service.exception;

public class DuplicatedException extends AccountServiceException {
    public DuplicatedException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
