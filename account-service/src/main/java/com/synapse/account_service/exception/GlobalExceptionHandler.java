package com.synapse.account_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.synapse.account_service.exception.dto.ExceptionResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger("ErrorLogger");

    private static final String LOG_FORMAT_INFO_PATTERN = "[🔵INFO] - (%s %s)\nExceptionType: %s\n %s: %s";
    private static final String LOG_FORMAT_WARN_PATTERN = "[🟠WARN] - (%s %s)\nExceptionType: %s\n %s: %s";
    private static final String LOG_FORMAT_ERROR_PATTERN = "[🔴ERROR] - (%s %s)\nExceptionType: %s\n %s: %s";

    @ExceptionHandler(AccountServiceException.class)
    public ResponseEntity<ExceptionResponse> handleAccountServiceException(AccountServiceException e, HttpServletRequest request) {
        logInfo(e, request);
        return ResponseEntity.status(e.getExceptionType().getStatus()).body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e, HttpServletRequest request) {
        logError(e, request);
        return ResponseEntity
                .status(ExceptionType.EXCEPTION.getStatus())
                .body(new ExceptionResponse(ExceptionType.EXCEPTION.getCode(), ExceptionType.EXCEPTION.getMessage()));
    }

    private void logInfo(AccountServiceException e, HttpServletRequest request) {
        log.info(String.format(LOG_FORMAT_INFO_PATTERN, 
                request.getMethod(), 
                request.getRequestURI(),
                e.getExceptionType(), 
                e.getClass().getName(), 
                e.getMessage()));
    }

    private void logWarn(AccountServiceException e, HttpServletRequest request) {
        log.warn(String.format(LOG_FORMAT_WARN_PATTERN, 
                request.getMethod(), 
                request.getRequestURI(),
                e.getExceptionType(), 
                e.getClass().getName(), 
                e.getMessage()));
    }

    private void logError(Exception e, HttpServletRequest request) {
        log.error(String.format(LOG_FORMAT_ERROR_PATTERN, 
                request.getMethod(), 
                request.getRequestURI(),
                e.getClass().getName(), 
                e.getMessage()));
    }
}
