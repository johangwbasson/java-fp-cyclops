package net.johanbasson.fp.api.users;

import cyclops.control.Maybe;
import cyclops.reactive.IO;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class JdbcUserRepository implements UserRepository {

    private final Sql2o sql2o;

    public JdbcUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public IO<Maybe<User>> findByEmail(String email) {
        return IO.of(() -> {
            try (Connection con = sql2o.open()) {
                return Maybe.ofNullable(con.createQuery("select")
                        .addColumnMapping("email", email)
                        .executeAndFetchFirst(User.class));
            }
        });
    }
}
