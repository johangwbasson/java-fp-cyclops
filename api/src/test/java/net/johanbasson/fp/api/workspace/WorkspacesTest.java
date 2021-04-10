package net.johanbasson.fp.api.workspace;

import cyclops.control.Maybe;
import cyclops.reactive.IO;
import net.johanbasson.fp.api.MockApplicationContext;
import net.johanbasson.fp.api.system.errors.FieldError;
import net.johanbasson.fp.api.types.Description;
import net.johanbasson.fp.api.types.Name;
import net.johanbasson.fp.api.users.Principal;
import net.johanbasson.fp.api.users.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static cyclops.control.Maybe.just;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WorkspacesTest {

    private final MockApplicationContext context = new MockApplicationContext();

    @Nested
    @DisplayName("Create workspace")
    class CreateWorkspace {

        @Test
        public void createValidRequest() throws ExecutionException, InterruptedException {
            // GIVEN
            Principal principal = new Principal(UUID.randomUUID(), Role.USER);
            when(context.getWorkspaceRepository().findByName(any(Principal.class), any(Name.class))).thenReturn(IO.of(Maybe.nothing()));

            // WHEN
            Workspaces.create(principal, Name.of("Finance"), Description.of("My financial documents"))
                    .apply(context)
                    .flatMap(res ->
                            res.fold(
                                    apiError -> {
                                        Assertions.fail("Unexpected error: " + apiError.getMessage());
                                        return IO.of(Boolean.TRUE);
                                    },
                                    created -> {
                                        System.out.println("1");
                                        assertThat(created).isNotNull();
                                        assertThat(created.getId()).isNotNull();
                                        verify(context.getCommandBus()).execute(any(CreateWorkspaceCommand.class));
                                        return IO.of(Boolean.TRUE);
                                    })
                    ).run();

        }

        @Test
        public void createNoPrincipal() {
            // WHEN
            Workspaces.create(null, Name.of("Finance"), Description.of("My financial documents"))
                    .apply(context)
                    .flatMap(res -> {
                                System.out.println(res);
                                return res.fold(apiError -> {
                                    org.assertj.core.api.Assertions.assertThat(apiError.getMessage()).isEqualTo("Validation error");
                                    org.assertj.core.api.Assertions.assertThat(apiError.getErrors()).contains(new FieldError("Principal", "Principal is required"));
                                    verify(context.getCommandBus(), times(0)).execute(any(CreateWorkspaceCommand.class));
                                    return IO.of(Boolean.TRUE);
                                }, created -> {
                                    fail("Unexpected workspace created");
                                    return IO.of(Boolean.TRUE);
                                });
                            }
                    ).run();
        }

        @Test
        public void createNoName() {
            // GIVEN
            Principal principal = new Principal(UUID.randomUUID(), Role.USER);

            // WHEN
            Workspaces.create(principal, null, Description.of("My financial documents"))
                    .apply(context)
                    .flatMap(res ->
                            res.fold(
                                    apiError -> {
                                        org.assertj.core.api.Assertions.assertThat(apiError.getMessage()).isEqualTo("Validation error");
                                        org.assertj.core.api.Assertions.assertThat(apiError.getErrors()).containsExactly(new FieldError("Name", "Name is required"));
                                        verify(context.getCommandBus(), times(0)).execute(any(CreateWorkspaceCommand.class));
                                        return IO.of(Boolean.TRUE);
                                    },
                                    created -> {
                                        fail("Unexpected workspace created");
                                        return IO.of(Boolean.TRUE);
                                    })
                    ).run();
        }

        @Test
        public void createDuplicate() {
            // GIVEN
            Principal principal = new Principal(UUID.randomUUID(), Role.USER);
            when(context.getWorkspaceRepository().findByName(any(Principal.class), any(Name.class)))
                    .thenReturn(IO.of(just(new Workspace(UUID.randomUUID(), "Finance", "Desc", LocalDateTime.now(), LocalDateTime.now()))));

            // WHEN
            Workspaces.create(principal, Name.of("Finance"), Description.of("My financial documents"))
                    .apply(context)
                    .flatMap(res ->
                            res.fold(
                                    apiError -> {
                                        org.assertj.core.api.Assertions.assertThat(apiError.getMessage()).isEqualTo("Workspace already exists");
                                        verify(context.getCommandBus(), times(0)).execute(any(CreateWorkspaceCommand.class));
                                        return IO.of(Boolean.TRUE);
                                    }, created -> {
                                        fail("Unexpected workspace created");
                                        return IO.of(Boolean.TRUE);
                                    }
                            )
                    ).run();
        }

    }

}