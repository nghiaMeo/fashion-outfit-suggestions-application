package com.example.common.exception;

import com.example.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for all REST endpoints
 * Catches and handles exceptions uniformly across the application
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle custom AppException
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<?>> handleAppException(AppException ex, WebRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("AppException: {} - {}", errorCode.getErrorCode(), ex.getMessage());

        ApiResponse<?> response = ApiResponse.error(
                errorCode.getCode(),
                ex.getMessage() != null ? ex.getMessage() : errorCode.getMessage()
        );

        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    /**
     * Handle authentication exception
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("AuthenticationException: {} - {}", errorCode.getErrorCode(), ex.getMessage());

        ApiResponse<?> response = ApiResponse.error(
                errorCode.getCode(),
                ex.getMessage() != null ? ex.getMessage() : errorCode.getMessage()
        );

        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    /**
     * Handle resource not found exception
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("ResourceNotFoundException: {} - {}", errorCode.getErrorCode(), ex.getMessage());

        ApiResponse<?> response = ApiResponse.error(
                errorCode.getCode(),
                ex.getMessage() != null ? ex.getMessage() : errorCode.getMessage()
        );

        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    /**
     * Handle validation exception (@Valid failed)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("MethodArgumentNotValidException: Validation failed");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<?> response = ApiResponse.error(
                ErrorCode.VALIDATION_FAILED.getCode(),
                "Validation failed",
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected exception occurred", ex);

        ApiResponse<?> response = ApiResponse.error(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                "An unexpected error occurred: " + ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());

        ApiResponse<?> response = ApiResponse.error(
                ErrorCode.INVALID_INPUT.getCode(),
                ex.getMessage() != null ? ex.getMessage() : ErrorCode.INVALID_INPUT.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
