package net.johanbasson.fp.api.users;

import cyclops.control.Maybe;
import io.jsonwebtoken.Claims;
import net.johanbasson.fp.api.system.errors.FieldError;
import net.johanbasson.fp.api.types.Validatable;

import java.util.UUID;

public class Principal implements Validatable {

    private final UUID id;
    private final Role role;

    public Principal(UUID id, Role role) {
        this.id = id;
        this.role = role;
    }

    public static Principal from(Claims claims) {
        return new Principal(UUID.fromString(claims.getSubject()),
                             Role.valueOf(claims.get("ROLE").toString()));
    }

    public UUID getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public Maybe<FieldError> validate() {
        if (id == null) {
            return Maybe.just(FieldError.of("principal", "Invalid principal specified"));
        }

        if (role == null) {
            return Maybe.just(FieldError.of("principal", "Invalid principal specified"));
        }

        return Maybe.nothing();
    }
}
