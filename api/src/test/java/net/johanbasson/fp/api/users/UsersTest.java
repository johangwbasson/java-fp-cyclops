package net.johanbasson.fp.api.users;

import cyclops.control.Maybe;
import cyclops.reactive.IO;
import io.jsonwebtoken.Jwts;
import net.johanbasson.fp.api.MockApplicationContext;
import net.johanbasson.fp.api.system.errors.ErrorType;
import net.johanbasson.fp.api.system.errors.FieldError;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UsersTest {

    public static final User ADMIN = new User(UUID.randomUUID(), "admin", BCrypt.hashpw("admin", BCrypt.gensalt()), Role.ADMINISTRATOR);

    private final MockApplicationContext context = new MockApplicationContext();

    @Nested
    @DisplayName("Authenticate")
    class Authenticate {

        @Test
        @DisplayName("Authenticate with valid credentials")
        public void validCredentials() {
            // GIVEN
            when(context.getUserRepository().findByEmail(anyString())).thenReturn(IO.of(Maybe.just(ADMIN)));

            // WHEN
            Users.authenticate(new AuthenticateUserCommand("admin", "admin"))
                    .apply(context)
                    .flatMap(res ->
                            res.fold(apiError -> {
                                fail("No token generated with either right!");
                                return IO.of(Boolean.TRUE);
                            }, jwtToken -> {
                                assertThat(jwtToken).isNotNull();
                                assertThat(jwtToken.token()).isNotEmpty().hasSizeGreaterThan(20);
                                assertThat(jwtToken.expires()).isGreaterThan(0);
                                return IO.of(Boolean.TRUE);
                            })
                    )
                    .run();
        }

        @Test
        @DisplayName("Authenticate with invalid email")
        public void invalidEmail() {
            when(context.getUserRepository().findByEmail("admin")).thenReturn(IO.of(Maybe.nothing()));

            Users.authenticate(new AuthenticateUserCommand("admin", "admin"))
                    .apply(context)
                    .flatMap(res ->
                            res.fold(
                                    error -> {
                                        Assertions.assertThat(error.getMessage()).isEqualTo(ErrorType.USER_NOT_FOUND.getMessage());
                                        return IO.of(Boolean.TRUE);
                                    },
                                    jwtToken -> {
                                        fail("Unexpected token received");
                                        return IO.of(Boolean.TRUE);
                                    })
                    )
                    .run();

        }

        @Test
        @DisplayName("Authenticate with invalid password")
        public void invalidPassword() {
            when(context.getUserRepository().findByEmail("admin")).thenReturn(IO.of(Maybe.nothing()));

            Users.authenticate(new AuthenticateUserCommand("admin", "admin1"))
                    .apply(context)
                    .flatMap(res ->
                            res.fold(
                                    error -> {
                                        Assertions.assertThat(error.getMessage()).isEqualTo(ErrorType.PASSWORDS_DOES_NOT_MATCH.getMessage());
                                        return IO.of(Boolean.TRUE);
                                    },
                                    jwtToken -> {
                                        fail("Unexpected token received");
                                        return IO.of(Boolean.TRUE);
                                    }
                            )
                    )
                    .run();
        }

        @Test
        @DisplayName("Authenticate with empty parameters")
        public void emptyParameters() {
            when(context.getUserRepository().findByEmail("admin")).thenReturn(IO.of(Maybe.just(ADMIN)));
            Users.authenticate(new AuthenticateUserCommand("", ""))
                    .apply(context)
                    .flatMap(res ->
                            res.fold(
                                    error -> {
                                        Assertions.assertThat(error.getErrors())
                                                .containsExactly(
                                                        new FieldError("email", "Email cannot be empty"),
                                                        new FieldError("password", "Password cannot be empty")
                                                );

                                        return IO.of(Boolean.TRUE);
                                    },
                                    jwtToken -> {
                                        fail("Unexpected token received");
                                        return IO.of(Boolean.TRUE);
                                    }
                            )
                    )
                    .run();
        }
    }

    @Nested
    @DisplayName("Authorize")
    class Authorize {

        @Test
        public void validJwtToken() {
            String token = Jwts.builder()
                    .setSubject(ADMIN.id().toString())
                    .claim("ROLE", ADMIN.role().name())
                    .signWith(context.getSecretKey()).compact();
            String headerValue = String.format("BEARER %s", token);

            Users.authorize(headerValue)
                    .apply(context)
                    .fold(
                            error -> {
                                fail("Unexpected api error");
                                return IO.of(Boolean.TRUE);
                            },
                            principal -> {
                                assertThat(principal).isNotNull();
                                assertThat(principal.getId()).isEqualTo(ADMIN.id());
                                assertThat(principal.getRole()).isEqualTo(ADMIN.role());
                                return IO.of(Boolean.TRUE);
                            }
                    )
                    .run();
        }

        @Test
        public void invalidJwtToken() {
            String headerValue = "BEARER 112232423423423423";

            Users.authorize(headerValue)
                    .apply(context)
                    .fold(
                            error -> {
                                Assertions.assertThat(error).isNotNull();
                                Assertions.assertThat(error.getMessage()).isEqualTo("Invalid JWT Token");
                                return IO.of(Boolean.TRUE);
                            },
                            principal -> fail("Unexpected principal")
                    )
                    .run();
        }

        @Test
        public void noBearer() {
            String headerValue = "ARER 11111111111111111111111";

            Users.authorize(headerValue)
                    .apply(context)
                    .fold(
                            error -> {
                                Assertions.assertThat(error).isNotNull();
                                Assertions.assertThat(error.getMessage()).isEqualTo("Invalid JWT Token");
                                return IO.of(Boolean.TRUE);
                            },
                            principal -> fail("Unexpected principal")
                    );
        }
    }
}