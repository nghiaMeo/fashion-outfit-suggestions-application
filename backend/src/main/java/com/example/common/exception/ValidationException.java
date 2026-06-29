package com.example.common.exception;

/**
 * Thrown when validation of input data fails
 */
public class ValidationException extends AppException {
    public ValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ValidationException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
