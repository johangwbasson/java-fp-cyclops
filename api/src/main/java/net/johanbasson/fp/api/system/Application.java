package net.johanbasson.fp.api.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import net.johanbasson.fp.api.config.Configuration;
import net.johanbasson.fp.api.config.ConfigurationLoader;
import net.johanbasson.fp.api.system.commandbus.CommandBus;
import net.johanbasson.fp.api.system.commandbus.CommandBusBuilder;
import net.johanbasson.fp.api.system.events.EventPort;
import net.johanbasson.fp.api.system.events.EventRepository;
import net.johanbasson.fp.api.system.events.JdbcEventRepository;
import net.johanbasson.fp.api.system.queue.CommandDispatcher;
import net.johanbasson.fp.api.system.queue.EventDispatcher;
import net.johanbasson.fp.api.users.JdbcUserRepository;
import net.johanbasson.fp.api.users.UserRepository;
import net.johanbasson.fp.api.workspace.JdbcWorkspaceRepository;
import net.johanbasson.fp.api.workspace.WorkspaceCommandHandlers;
import net.johanbasson.fp.api.workspace.WorkspaceRepository;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.flywaydb.core.Flyway;
import org.sql2o.Sql2o;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.Key;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Application implements ApplicationContext {

    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final Sql2o sql2o;
    private final ObjectMapper objectMapper;
    private final Configuration configuration;
    private final HikariDataSource dataSource;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final EventPort eventPort;
    private final EventBus eventBus;
    private final CommandBus commandBus;
    private final EventDispatcher eventDispatcher;
    private final CommandDispatcher commandDispatcher;

    public static Application initialize() throws ConfigurationException, IOException, TimeoutException {
        return new Application();
    }

    private Application() throws ConfigurationException, IOException, TimeoutException {
        objectMapper = new ObjectMapper();
        configuration = ConfigurationLoader.load("config.properties");
        dataSource = getHikariDataSource(configuration);
        sql2o = new Sql2o(dataSource);
        userRepository = new JdbcUserRepository(sql2o);
        eventRepository = new JdbcEventRepository(sql2o);
        eventPort = new EventPort(eventRepository, objectMapper);
        eventBus = eventBus(eventPort);
        eventDispatcher = new EventDispatcher(configuration);
        commandDispatcher = new CommandDispatcher(configuration);

        workspaceRepository = new JdbcWorkspaceRepository(sql2o);
        commandBus = commandBus(new WorkspaceCommandHandlers(workspaceRepository, eventDispatcher));
    }

    private HikariDataSource getHikariDataSource(Configuration configuration) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPassword(configuration.database().password());
        hikariConfig.setJdbcUrl(configuration.database().url());
        hikariConfig.setUsername(configuration.database().username());
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        HikariDataSource ds = new HikariDataSource(hikariConfig);
        Flyway.configure().dataSource(ds).load().migrate();
        return ds;
    }

    private EventBus eventBus(EventPort eventPort) {
        EventBus eventBus = new AsyncEventBus(Executors.newCachedThreadPool());
        eventBus.register(eventPort);
        return eventBus;
    }

    private CommandBus commandBus(WorkspaceCommandHandlers workspaceCommandHandlers) {
        return new CommandBusBuilder()
                .executorService(Executors.newCachedThreadPool())
                .registerCommandHandler(workspaceCommandHandlers)
                .build();
    }

    public void shutdown() {
        dataSource.close();
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
        return configuration;
    }

    @Override
    public Sql2o getSql2o() {
        return sql2o;
    }

    @Override
    public WorkspaceRepository getWorkspaceRepository() {
        return workspaceRepository;
    }
}
