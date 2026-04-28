package com.example.wardrobeservices.exception;

import com.example.wardrobeservices.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ErrorResponse> handlingRuntimeException(Exception exception) {
        log.error("Exception: ", exception);
        ErrorResponse apiResponse = new ErrorResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ErrorResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse apiResponse = new ErrorResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }

    /**
     * Handle validation failures from method argument binding and produce a standardized error response.
     *
     * The response uses the `ErrorCode.INVALID_KEY` code. The response message is taken from the first
     * field error's default message when available; otherwise the literal "Validation failed" is used.
     *
     * @param exception the MethodArgumentNotValidException containing validation error details; its first field error's default message is used for the response message when present
     * @return a ResponseEntity containing an ErrorResponse with `code = ErrorCode.INVALID_KEY.getCode()`, the chosen message, and HTTP status `ErrorCode.INVALID_KEY.getHttpStatus()`
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handlingValidation(MethodArgumentNotValidException exception) {
        String message = exception.getFieldError() != null
                ? exception.getFieldError().getDefaultMessage()
                : "Validation failed";

        ErrorResponse apiResponse = ErrorResponse.builder()
                .code(ErrorCode.INVALID_KEY.getCode())
                .message(message)
                .build();

        return ResponseEntity.status(ErrorCode.INVALID_KEY.getHttpStatus()).body(apiResponse);
    }

    /**
     * Handle requests for which no controller handler (endpoint) was found.
     *
     * @param exception the NoHandlerFoundException raised when the request URL or method has no matching handler
     * @return a ResponseEntity containing an ErrorResponse populated from ErrorCode.ENDPOINT_NOT_FOUND and using its HTTP status
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    ResponseEntity<ErrorResponse> handlingNoHandlerFoundException(NoHandlerFoundException exception) {
        log.warn("No handler found: {}", exception.getMessage());
        return createErrorResponse(ErrorCode.ENDPOINT_NOT_FOUND);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErrorResponse> handlingMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.warn("Method not supported: {}", exception.getMessage());
        return createErrorResponse(ErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ErrorResponse> handlingMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        log.warn("Media type not supported: {}", exception.getMessage());
        return createErrorResponse(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> handlingMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.warn("Message not readable: {}", exception.getMessage());
        return createErrorResponse(ErrorCode.MALFORMED_REQUEST_BODY);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    ResponseEntity<ErrorResponse> handlingMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        log.warn("Missing parameter: {}", exception.getMessage());
        return createErrorResponse(ErrorCode.MISSING_REQUEST_PARAMETER);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    ResponseEntity<ErrorResponse> handlingMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.warn("Type mismatch: {}", exception.getMessage());
        return createErrorResponse(ErrorCode.TYPE_MISMATCH);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode) {
        ErrorResponse apiResponse = new ErrorResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }
}
