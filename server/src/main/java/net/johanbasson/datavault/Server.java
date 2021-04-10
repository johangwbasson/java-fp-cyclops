package net.johanbasson.datavault;

import io.javalin.Javalin;
import net.johanbasson.fp.api.system.ApplicationContext;
import net.johanbasson.fp.api.system.errors.ErrorMessage;
import net.johanbasson.fp.api.users.AuthenticateUserCommand;
import net.johanbasson.fp.api.users.Users;
import org.eclipse.jetty.http.HttpStatus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final ApplicationContext applicationContext;
    private final Javalin application;

    private final ExecutorService workers = Executors.newFixedThreadPool(20);

    public Server(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

        application = Javalin.create(config -> {
            config.enableCorsForAllOrigins();
        });

        application.post("/authenticate", context -> {
            AuthenticateUserCommand command = context.bodyAsClass(AuthenticateUserCommand.class);
            Users.authenticate(command)
                    .apply(applicationContext)
                    .runAsync(workers)
                    .fold(
                            result -> result.fold(
                                    apiError -> {
                                        context.status(HttpStatus.BAD_REQUEST_400).json(apiError.toErrorMessage());
                                        return null;
                                    },
                                    jwtToken -> {
                                        context.status(HttpStatus.OK_200).json(jwtToken);
                                        return null;
                                    }
                            ),
                            throwable -> {
                                context.status(HttpStatus.INTERNAL_SERVER_ERROR_500).json(new ErrorMessage(String.format("Internal Server Error: %s", throwable.getLocalizedMessage())));
                                return null;
                            }
                    );
        });

    }

    public void start() {
        application.start(applicationContext.getConfiguration().server().port());
    }

    public void stop() {
        application.stop();
    }

}
