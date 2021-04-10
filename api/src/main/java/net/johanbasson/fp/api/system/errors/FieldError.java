package net.johanbasson.fp.api.system.errors;

public record FieldError(String field, String message) {

    public static FieldError of(String field, String message) {
        return new FieldError(field, message);
    }
}
