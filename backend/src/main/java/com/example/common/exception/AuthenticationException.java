package com.example.common.exception;

/**
 * Thrown when authentication or authorization fails
 */
public class AuthenticationException extends AppException {
    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthenticationException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
