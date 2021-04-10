package net.johanbasson.fp.api.system.errors;


import java.util.HashSet;
import java.util.Set;

public class ValidationErrors extends ErrorMessage {

    private Set<FieldError> errors = new HashSet<>();

    public ValidationErrors() {
        super("Validation error(s)");
    }

    public ValidationErrors(String message, Set<FieldError> errors) {
        super(message);
        this.errors = errors;
    }

    public ValidationErrors add(String field, String message) {
        errors.add(new FieldError(field, message));
        return this;
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public ApiError toApiError() {
        return new ApiError(ErrorType.VALIDATION_ERROR, errors);
    }

    public Set<FieldError> getErrors() {
        return errors;
    }
}

