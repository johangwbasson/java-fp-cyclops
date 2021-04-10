package net.johanbasson.fp.api.system.errors;

public enum ErrorType {
    USER_NOT_FOUND("Invalid email or password specified"),
    PASSWORDS_DOES_NOT_MATCH("Invalid email or password specified"),
    AUTHORIZATION_HEADER_IS_REQUIRED("Authorization header is required"),
    INVALID_JWT_TOKEN("Invalid JWT Token"),
    INVALID_EMAIL("Invalid email address"),
    PASSWORD_CANNOT_BE_EMPTY("Password cannot be empty"),
    PASSWORD_SHOULD_BE_AT_LEAST_EIGHT_CHARS("Password should be at least 8 characters"),
    USER_ALREADY_EXISTS("User already exists"),
    EMAIL_CANNOT_BE_EMPTY("Email cannot be empty"),
    INTERNAL_SERVER_ERROR("Internal Server Error"),
    UNAUTHORIZED("Unauthorized"),
    NAME_CANNOT_BE_EMPTY("Name cannot be empty"),
    WORKSPACE_ALREADY_EXISTS("Workspace already exists"),
    CONFIGURATION_ERROR("Configuration error"),
    VALIDATION_ERROR("Validation error"),
    INVALID_AUTH_HEADER("Invalid authorization header");

    ErrorType(String message) {
        this.message = message;
    }

    private final String message;

    public String getMessage() {
        return message;
    }
}
