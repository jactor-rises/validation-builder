package com.github.jactorrises.builder.sample;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class NamedEraBuilderTest {

    @Test
    void shouldNotInitNamedEraWithoutName() {
        assertThatIllegalStateException().isThrownBy(new NamedEraBuilder()::build)
                .withMessage("A named era must have a name");
    }

    @Test
    void shouldNotInitNamedAreatWithoutBeginning() {
        assertThatIllegalStateException().isThrownBy(new NamedEraBuilder().withName("An era")::build)
                .withMessage("An era must have a beginning");
    }

    @Test
    void shouldNotInitNamedAreatWithBeginningAfterTheEnd() {
        assertThatIllegalStateException().isThrownBy(() ->
                new NamedEraBuilder()
                        .withName("The era")
                        .withBeginning(LocalDate.now())
                        .withEnd(LocalDate.now().minusDays(1))
                        .build()
        ).withMessage("The era cannot end before it is started");
    }

    @Test
    void shouldInitValidInstanceOnlyApplyingNameAndBeginning() {
        assertThat(new NamedEraBuilder().withName("An era").withBeginning(LocalDate.now()).build()).isNotNull();
    }
}