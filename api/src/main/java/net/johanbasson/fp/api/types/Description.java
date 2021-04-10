package net.johanbasson.fp.api.types;

import cyclops.control.Maybe;
import net.johanbasson.fp.api.system.errors.FieldError;

public record Description(String description) implements Validatable {

    public static Description of(String value) {
        return new Description(value);
    }

    public String asString() {
        return description;
    }

    @Override
    public Maybe<FieldError> validate() {
        if (description != null && description.length() > 512) {
            return Maybe.just(FieldError.of("description", "Description cannot be longer than 255 characters"));
        }
        return Maybe.nothing();
    }
}
