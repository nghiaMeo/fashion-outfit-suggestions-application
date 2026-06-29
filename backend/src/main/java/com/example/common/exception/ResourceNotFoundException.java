package com.example.common.exception;

/**
 * Thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ResourceNotFoundException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
