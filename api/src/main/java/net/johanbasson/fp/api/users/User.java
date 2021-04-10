package net.johanbasson.fp.api.users;

import java.util.UUID;

public record User(UUID id, String email, String password, Role role) {

}
