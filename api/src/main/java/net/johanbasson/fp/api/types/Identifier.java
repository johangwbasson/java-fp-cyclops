package net.johanbasson.fp.api.types;

import cyclops.control.Maybe;
import net.johanbasson.fp.api.system.errors.FieldError;

import java.util.UUID;

public final class Identifier implements Validatable {

    private final UUID value;

    private Identifier(UUID value) {
        this.value = value;
    }

    public static Identifier generate() {
        return new Identifier(UUID.randomUUID());
    }

    public static Identifier of(String value) {
        return new Identifier(UUID.fromString(value));
    }

    public static Identifier of(UUID value) {
        return new Identifier(value);
    }

    public String asString() {
        return value.toString();
    }

    public UUID asUUID() {
        return value;
    }

    @Override
    public Maybe<FieldError> validate() {
        if (value == null) {
            return Maybe.just(FieldError.of("id", "Identifier cannot be null"));
        }

        return Maybe.nothing();
    }
}
