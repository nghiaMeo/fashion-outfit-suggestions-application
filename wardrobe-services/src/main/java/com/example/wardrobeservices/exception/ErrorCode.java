package com.example.wardrobeservices.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(400, "Invalid request field", HttpStatus.BAD_REQUEST),
    USER_EXISTED(400, "User already existed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(404, "User is not found", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(403, "You do not have permission", HttpStatus.FORBIDDEN),
    OUTFIT_NOT_FOUND(404, "Outfit is not found", HttpStatus.NOT_FOUND),
    ITEM_NOT_FOUND(404, "Item is not found", HttpStatus.NOT_FOUND),
    ENDPOINT_NOT_FOUND(404, "Endpoint not found", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(405, "HTTP method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported media type", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    MALFORMED_REQUEST_BODY(400, "Malformed request body", HttpStatus.BAD_REQUEST),
    MISSING_REQUEST_PARAMETER(400, "Missing required request parameter", HttpStatus.BAD_REQUEST),
    TYPE_MISMATCH(400, "Argument type mismatch", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
