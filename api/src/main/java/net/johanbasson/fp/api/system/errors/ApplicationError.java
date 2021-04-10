package net.johanbasson.fp.api.system.errors;

import java.util.Set;

public interface ApplicationError {

    String getMessage();

    Set<FieldError> getErrors();
}
