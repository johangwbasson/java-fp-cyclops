package net.johanbasson.fp.api.workspace;

import com.oath.cyclops.types.factory.Unit;
import cyclops.control.Either;
import cyclops.data.tuple.Tuple2;
import cyclops.function.Function1;
import cyclops.matching.Api;
import cyclops.reactive.IO;
import net.johanbasson.fp.api.system.ApplicationContext;
import net.johanbasson.fp.api.system.Created;
import net.johanbasson.fp.api.system.errors.ApiError;
import net.johanbasson.fp.api.system.errors.ErrorType;
import net.johanbasson.fp.api.system.errors.ValidationErrors;
import net.johanbasson.fp.api.types.Description;
import net.johanbasson.fp.api.types.Identifier;
import net.johanbasson.fp.api.types.Name;
import net.johanbasson.fp.api.users.Principal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static net.johanbasson.fp.api.system.errors.ValidationUtil.validate;

public final class Workspaces {


    public static Function1<ApplicationContext, IO<Either<ApiError, Created>>> create(Principal principal, Name name, Description description) {
        return context ->
                validate(
                        Arrays.asList(
                                Tuple2.of("Principal", principal),
                                Tuple2.of("Name", name),
                                Tuple2.of("Description", description)
                        ))
                        .toEither(Boolean.FALSE)
                        .fold(
                                new Function<Boolean, IO<Either<ApiError, Created>>>() {
                                    @Override
                                    public IO<Either<ApiError, Created>> apply(Boolean aBoolean) {
//                                        return context.getWorkspaceRepository().findByName(principal, name)
//                                                .flat
//                                                .flatMap(maybeWs -> {
//                                                    return process(maybeWs);
//                                                })
                                        return IO.of(Either.right(new Created(Identifier.generate())));
                                    }
                                },
                                new Function<ValidationErrors, IO<Either<ApiError, Created>>>() {
                                    @Override
                                    public IO<Either<ApiError, Created>> apply(ValidationErrors validationErrors) {
                                        return IO.of(Either.left(validationErrors.toApiError()));
                                    }
                                }
                        );
//                .fold(
//                        (Function<Boolean, IO<Either<ApiError, Created>>>) aBoolean ->
//                                context.getWorkspaceRepository().findByName(principal, name)
//                                        .flatMap(maybeWorkspace -> maybeWorkspace
//                                                .toEither(Identifier.generate())
//                                                .swap()
//                                                .fold(new Function<Workspace, IO<Either<ApiError, Created>>>() {
//                                                    @Override
//                                                    public IO<Either<ApiError, Created>> apply(Workspace workspace) {
//                                                        return IO.of(Either.left(ApiError.of(ErrorType.WORKSPACE_ALREADY_EXISTS)));
//                                                    }
//                                                }, new Function<Identifier, IO<Either<ApiError, Created>>>() {
//                                                    @Override
//                                                    public IO<Either<ApiError, Created>> apply(Identifier identifier) {
//                                                        Created created = new Created(identifier);
//                                                        context.getCommandBus().execute(new CreateWorkspaceCommand(principal, name, description, identifier));
//                                                        return IO.of(Either.right(created));
//                                                    }
//                                                })
//                                        ),
//                        (Function<ValidationErrors, Object>) validationErrors -> {
//                            return Either.left(validationErrors.toApiError());
//                        }
//                );
//                .match(
//                        unit -> context.getWorkspaceRepository().findByName(principal, name)
//                                .flatMap(maybeWorkspace -> {
//                                    Either<ApiError, Created> res = maybeWorkspace
//                                            .toEither(Identifier::generate)
//                                            .invert()
//                                            .match(
//                                                    workspace -> Either.left(ApiError.of(ErrorType.WORKSPACE_ALREADY_EXISTS)),
//                                                    id -> {
//                                                        Created created = new Created(id);
//                                                        context.getCommandBus().execute(new CreateWorkspaceCommand(principal, name, description, id));
//                                                        return Either.right(created);
//                                                    });
//                                    return IO.io(res);
//                                }),
//                        validationErrors -> IO.io(Either.left(validationErrors.toApiError()))
//                );
    }

    private static Either<ApiError, Created> process(cyclops.control.Maybe<Workspace> maybeWs) {
        return maybeWs.toEither(ApiError.of(ErrorType.WORKSPACE_ALREADY_EXISTS))
                .fold(
                        new Function<ApiError, Either<ApiError, Created>>() {
                            @Override
                            public Either<ApiError, Created> apply(ApiError apiError) {
                                return Either.left(apiError);
                            }
                        },
                        new Function<Workspace, Either<ApiError, Created>>() {
                            @Override
                            public Either<ApiError, Created> apply(Workspace workspace) {
                                return Either.right(new Created(Identifier.generate()));
                            }
                        }
                );
    }

    public static Function1<ApplicationContext, IO<Either<ApiError, List<Workspace>>>> list(Principal principal) {
        return context -> validate(Collections.singletonList(Tuple2.of("Principal", principal)))
                .toEither(Boolean.TRUE)
                .fold(
                        (Function<Boolean, IO<Either<ApiError, List<Workspace>>>>) aBoolean -> context.getWorkspaceRepository().list(principal).map(Either::right),
                        (Function<ValidationErrors, IO<Either<ApiError, List<Workspace>>>>) validationErrors -> IO.of(Either.left(validationErrors.toApiError()))
                );
    }

}
