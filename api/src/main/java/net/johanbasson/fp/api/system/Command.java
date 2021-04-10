package net.johanbasson.fp.api.system;

import net.johanbasson.fp.api.system.errors.ValidationErrors;

public interface Command {

    ValidationErrors validate();

}
