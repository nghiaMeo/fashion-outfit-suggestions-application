package com.example.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(400, "Invalid request field", HttpStatus.BAD_REQUEST),
    USER_EXISTED(400, "User already existed", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(400, "Email already existed", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTED(400, "Username already existed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(404, "User is not found", HttpStatus.NOT_FOUND),
    CONVERSATION_NOT_FOUND(404, "Conversation is not found", HttpStatus.NOT_FOUND),
    NOTIFICATION_NOT_FOUND(404, "Notification is not found", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    CANNOT_FRIEND_SELF(400, "Cannot send friend request to yourself", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_ALREADY_SENT(400, "Friend request already sent", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_NOT_FOUND(400, "Friend request not found", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(403, "You do not have permission", HttpStatus.FORBIDDEN),
    OUTFIT_NOT_FOUND(404, "Outfit is not found", HttpStatus.NOT_FOUND),
    OUTFIT_PRIVATE(401, "Outfit is private", HttpStatus.UNAUTHORIZED),
    ITEM_NOT_FOUND(404, "Item is not found", HttpStatus.NOT_FOUND),
    ENDPOINT_NOT_FOUND(404, "Endpoint not found", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(405, "HTTP method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported media type", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    MALFORMED_REQUEST_BODY(400, "Malformed request body", HttpStatus.BAD_REQUEST),
    MISSING_REQUEST_PARAMETER(400, "Missing required request parameter", HttpStatus.BAD_REQUEST),
    TYPE_MISMATCH(400, "Argument type mismatch", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(401, "Invalid email or password", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(401, "Refresh token has expired", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_FOUND(404, "Refresh token is not found", HttpStatus.NOT_FOUND),
    OAUTH2_INVALID_TOKEN(401, "Invalid OAuth2 token", HttpStatus.UNAUTHORIZED),
    WRONG_PASSWORD(400, "Old password is incorrect", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED(400, "OTP has expired or does not exist", HttpStatus.BAD_REQUEST),
    OTP_INVALID(400, "OTP is incorrect", HttpStatus.BAD_REQUEST),
    OTP_RATE_LIMITED(429, "Please wait 1 minute before requesting a new OTP", HttpStatus.TOO_MANY_REQUESTS),
    RATE_LIMITED(429, "Too many requests, please try again later", HttpStatus.TOO_MANY_REQUESTS),
    ACCOUNT_LOCKED(423, "Account is temporarily locked due to too many failed login attempts", HttpStatus.LOCKED);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
