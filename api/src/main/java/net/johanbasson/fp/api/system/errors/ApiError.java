package net.johanbasson.fp.api.system.errors;


import java.util.Set;

public class ApiError implements ApplicationError {
    private final ErrorType errorType;
    private Set<FieldError> fieldErrors;

    public ApiError(ErrorType errorType, Set<FieldError> fieldErrors) {
        this.errorType = errorType;
        this.fieldErrors = fieldErrors;
    }

    public ApiError(ErrorType errorType) {
        this.errorType = errorType;
    }

    @Override
    public String getMessage() {
        return errorType.getMessage();
    }

    @Override
    public Set<FieldError> getErrors() {
        return fieldErrors;
    }

    public static ApiError of(ErrorType errorType) {
        return new ApiError(errorType);
    }

    public ErrorMessage toErrorMessage() {
        if (fieldErrors.isEmpty()) {
            return new ErrorMessage(getMessage());
        } else {
            return new ValidationErrors(getMessage(), fieldErrors);
        }
    }
}
