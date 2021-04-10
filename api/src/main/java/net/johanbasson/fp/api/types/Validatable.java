package net.johanbasson.fp.api.types;

import cyclops.control.Maybe;
import net.johanbasson.fp.api.system.errors.FieldError;

public interface Validatable {

    Maybe<FieldError> validate();

}
