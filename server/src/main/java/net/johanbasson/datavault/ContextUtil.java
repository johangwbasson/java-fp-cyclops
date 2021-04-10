package net.johanbasson.datavault;

import cyclops.control.Either;
import io.javalin.http.Context;
import net.johanbasson.fp.api.system.errors.ApiError;
import net.johanbasson.fp.api.system.errors.ErrorMessage;
import net.johanbasson.fp.api.users.JwtToken;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public final class ContextUtil {

    public static BiFunction<Either<ApiError, JwtToken>, Throwable, Object> ok(@NotNull Context context) {
        return (result, throwable) -> {
            if (throwable != null) {
                context.status(HttpStatus.INTERNAL_SERVER_ERROR_500).json(new ErrorMessage(String.format("Internal Server Error: %s", throwable.getLocalizedMessage())));
            } else {
                result.fold(
                        apiError -> {
                            context.status(HttpStatus.BAD_REQUEST_400).json(apiError.toErrorMessage());
                            return null;
                        },
                        jwtToken -> {
                            context.status(HttpStatus.OK_200).json(jwtToken);
                            return null;
                        }
                );
            }
            return null;
        };
    }

}
