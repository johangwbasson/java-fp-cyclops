package net.johanbasson.fp.api.system.events;

import net.johanbasson.fp.api.users.Principal;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class JdbcEventRepository implements EventRepository {

    private final Sql2o sql2o;

    public JdbcEventRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void append(Principal principal, String type, String payload) {
        try (Connection con = sql2o.open()) {
            con.createQuery("INSERT INTO events() VALUES (event_date, event_type, user_id) VALUES (now(), :type, :user, :payload)")
                    .addParameter("type", type)
                    .addParameter("user", principal.getId())
                    .addParameter("payload", payload)
                    .executeUpdate();
        }
    }
}
