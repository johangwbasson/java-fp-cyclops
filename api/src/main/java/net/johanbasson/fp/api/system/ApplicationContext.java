package net.johanbasson.fp.api.system;

import net.johanbasson.fp.api.config.Configuration;
import net.johanbasson.fp.api.system.commandbus.CommandBus;
import net.johanbasson.fp.api.users.UserRepository;
import net.johanbasson.fp.api.workspace.WorkspaceRepository;
import org.sql2o.Sql2o;

import java.security.Key;

public interface ApplicationContext {

    Sql2o getSql2o();

    WorkspaceRepository getWorkspaceRepository();

    void shutdown();

    CommandBus getCommandBus();

    UserRepository getUserRepository();

    Key getSecretKey();

    Configuration getConfiguration();
}
