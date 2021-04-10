package net.johanbasson.fp.api.users;

import cyclops.control.Maybe;
import cyclops.reactive.IO;

public interface UserRepository {

    IO<Maybe<User>> findByEmail(String email);

}
