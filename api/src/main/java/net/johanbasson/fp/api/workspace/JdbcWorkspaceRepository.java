package net.johanbasson.fp.api.workspace;

import cyclops.control.Maybe;
import cyclops.reactive.IO;
import net.johanbasson.fp.api.types.Description;
import net.johanbasson.fp.api.types.Identifier;
import net.johanbasson.fp.api.types.Name;
import net.johanbasson.fp.api.users.Principal;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

public class JdbcWorkspaceRepository implements WorkspaceRepository {

    private final Sql2o sql2o;

    public JdbcWorkspaceRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public IO<Maybe<Workspace>> findByName(Principal principal, Name name) {
        return IO.of(() -> {
            try (Connection con = sql2o.open()) {
                return
                        Maybe.ofNullable(
                                con.createQuery("SELECT id, name, description, created, modified FROM workspaces WHERE user_id = :user AND name = :name")
                                        .addParameter("user", principal.getId())
                                        .addParameter("name", name.asString())
                                        .executeAndFetchFirst(Workspace.class)
                        );
            }
        });
    }

    @Override
    public IO<Integer> add(Principal principal, Identifier id, Name name, Description description) {
        return IO.of(() -> {
            try (Connection con = sql2o.open()) {
                return con.createQuery("INSERT INTO workspaces (id, user_id, name, description, created. modified, deleted) VALUES (:id, :user, :name, :desc, now(), now(), false)")
                        .addParameter("id", id.asUUID())
                        .addParameter("user_id", principal.getId())
                        .addParameter("name", name)
                        .addParameter("desc", description)
                        .executeUpdate()
                        .getResult();
            }
        });
    }

    @Override
    public IO<List<Workspace>> list(Principal principal) {
        return IO.of( () -> {
            try (Connection con = sql2o.open()) {
                return con.createQuery("SELECT id, name, description, created, modified FROM workspaces WHERE user_id = :user AND deleted = false ORDER BY name")
                        .addParameter("user", principal.getId())
                        .executeAndFetch(Workspace.class);
            }
        });
    }
}
