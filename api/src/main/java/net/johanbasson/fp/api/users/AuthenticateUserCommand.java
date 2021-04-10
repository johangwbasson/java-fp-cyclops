package net.johanbasson.fp.api.users;

import net.johanbasson.fp.api.system.Command;
import net.johanbasson.fp.api.system.errors.ValidationErrors;
import org.apache.commons.lang.StringUtils;

public record AuthenticateUserCommand(String email, String password) implements Command {

    public ValidationErrors validate() {
        ValidationErrors errors = new ValidationErrors();
        if (StringUtils.isEmpty(email)) {
            errors.add("email", "Email cannot be empty");
        }

        if (StringUtils.isEmpty(password)) {
            errors.add("password", "Password cannot be empty");
        }

        return errors;
    }
}
