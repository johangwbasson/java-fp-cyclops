package net.johanbasson.fp.api.system.errors;

import cyclops.data.tuple.Tuple2;
import net.johanbasson.fp.api.types.Name;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class ValidationUtilTest {

    @Test
    public void testNoError() {
        ValidationUtil.validate(Collections.singletonList(Tuple2.of("name", Name.of("John"))))
                .fold(
                        unit -> {
                            // Expected
                            return Boolean.TRUE;
                        },
                        validationErrors -> {
                            fail("There should be no errors but received errors");
                            return Boolean.TRUE;
                        });
    }

    @Test
    public void testError() {
        ValidationUtil.validate(Collections.singletonList(Tuple2.of("name", Name.of("de"))))
                .fold(
                        validationErrors -> {
                            assertThat(validationErrors.hasErrors()).isTrue();
                            return Boolean.TRUE;
                        },
                        unit -> {
                            fail("Expected errors to be returned");
                            return Boolean.TRUE;
                        }
                );

    }

}