package com.example.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Centralized Error Code Enum
 * All API errors must use one of these codes for consistency
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Authentication & Authorization
    INVALID_CREDENTIALS(401, "ERR_INVALID_CREDENTIALS", "Email or password is incorrect", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(401, "ERR_TOKEN_EXPIRED", "Access token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(401, "ERR_TOKEN_INVALID", "Invalid or malformed token", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(403, "ERR_UNAUTHORIZED", "Unauthorized access", HttpStatus.FORBIDDEN),
    
    // User
    USER_NOT_FOUND(404, "ERR_USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(409, "ERR_USER_ALREADY_EXISTS", "User with this email already exists", HttpStatus.CONFLICT),
    USER_INACTIVE(400, "ERR_USER_INACTIVE", "User account is inactive", HttpStatus.BAD_REQUEST),
    
    // Validation
    INVALID_INPUT(400, "ERR_INVALID_INPUT", "Invalid input provided", HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED(400, "ERR_VALIDATION_FAILED", "Validation failed", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD(400, "ERR_MISSING_REQUIRED_FIELD", "Required field is missing", HttpStatus.BAD_REQUEST),
    
    // Wardrobe
    ITEM_NOT_FOUND(404, "ERR_ITEM_NOT_FOUND", "Item not found", HttpStatus.NOT_FOUND),
    OUTFIT_NOT_FOUND(404, "ERR_OUTFIT_NOT_FOUND", "Outfit not found", HttpStatus.NOT_FOUND),
    INVALID_ITEM_TYPE(400, "ERR_INVALID_ITEM_TYPE", "Invalid item type", HttpStatus.BAD_REQUEST),
    DUPLICATE_ITEM(409, "ERR_DUPLICATE_ITEM", "This item already exists", HttpStatus.CONFLICT),
    
    // Social
    FRIENDSHIP_NOT_FOUND(404, "ERR_FRIENDSHIP_NOT_FOUND", "Friendship request not found", HttpStatus.NOT_FOUND),
    ALREADY_FRIENDS(409, "ERR_ALREADY_FRIENDS", "Already friends with this user", HttpStatus.CONFLICT),
    FRIENDSHIP_PENDING(409, "ERR_FRIENDSHIP_PENDING", "Friendship request already pending", HttpStatus.CONFLICT),
    CANNOT_FRIEND_SELF(400, "ERR_CANNOT_FRIEND_SELF", "Cannot send friend request to yourself", HttpStatus.BAD_REQUEST),
    
    // Chat & Messaging
    MESSAGE_NOT_FOUND(404, "ERR_MESSAGE_NOT_FOUND", "Message not found", HttpStatus.NOT_FOUND),
    CONVERSATION_NOT_FOUND(404, "ERR_CONVERSATION_NOT_FOUND", "Conversation not found", HttpStatus.NOT_FOUND),
    
    // File Upload
    FILE_UPLOAD_FAILED(400, "ERR_FILE_UPLOAD_FAILED", "File upload failed", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(400, "ERR_INVALID_FILE_TYPE", "Invalid file type", HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED(400, "ERR_FILE_SIZE_EXCEEDED", "File size exceeds maximum allowed", HttpStatus.BAD_REQUEST),
    
    // Notification
    NOTIFICATION_NOT_FOUND(404, "ERR_NOTIFICATION_NOT_FOUND", "Notification not found", HttpStatus.NOT_FOUND),
    
    // System
    INTERNAL_SERVER_ERROR(500, "ERR_INTERNAL_SERVER_ERROR", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR(500, "ERR_DATABASE_ERROR", "Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_API_ERROR(502, "ERR_EXTERNAL_API_ERROR", "External API request failed", HttpStatus.BAD_GATEWAY),
    SERVICE_UNAVAILABLE(503, "ERR_SERVICE_UNAVAILABLE", "Service is temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE);

    private final int code;
    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;
}
