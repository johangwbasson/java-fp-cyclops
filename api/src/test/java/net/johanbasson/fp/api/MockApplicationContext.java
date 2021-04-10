package net.johanbasson.fp.api;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import net.johanbasson.fp.api.config.Configuration;
import net.johanbasson.fp.api.config.Database;
import net.johanbasson.fp.api.config.RabbitMq;
import net.johanbasson.fp.api.config.Server;
import net.johanbasson.fp.api.system.ApplicationContext;
import net.johanbasson.fp.api.system.commandbus.CommandBus;
import net.johanbasson.fp.api.users.UserRepository;
import net.johanbasson.fp.api.workspace.WorkspaceRepository;
import org.sql2o.Sql2o;

import javax.crypto.SecretKey;
import java.security.Key;

import static org.mockito.Mockito.mock;

public class MockApplicationContext implements ApplicationContext {

    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final UserRepository userRepository = mock(UserRepository.class);
    private final WorkspaceRepository workspaceRepository = mock(WorkspaceRepository.class);
    private final Sql2o sql2o = mock(Sql2o.class);
    private final CommandBus commandBus = mock(CommandBus.class);

    @Override
    public Sql2o getSql2o() {
        return sql2o;
    }

    @Override
    public WorkspaceRepository getWorkspaceRepository() {
        return workspaceRepository;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public CommandBus getCommandBus() {
        return commandBus;
    }

    @Override
    public UserRepository getUserRepository() {
        return userRepository;
    }

    @Override
    public Key getSecretKey() {
        return secretKey;
    }

    @Override
    public Configuration getConfiguration() {
        return new Configuration(new Database("", "", ""), new Server(7122), new RabbitMq("", 111, "", ""));
    }
}
