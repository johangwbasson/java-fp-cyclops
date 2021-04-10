package net.johanbasson.fp.api.system.errors;

import java.util.Set;

public class ErrorMessage {

    private final String status = "Failure";
    private final String message;

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
