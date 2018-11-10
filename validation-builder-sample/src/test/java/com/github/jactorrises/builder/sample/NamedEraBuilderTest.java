package com.github.jactorrises.builder.sample;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

@DisplayName("NamedEraBuilder")
class NamedEraBuilderTest {

    @DisplayName("should not build NamedEra without a naming it")
    @Test
    void shouldNotInitNamedEraWithoutName() {
        assertThatIllegalStateException().isThrownBy(new NamedEraBuilder()::build)
                .withMessage("A named era must have a name");
    }

    @DisplayName("should not build NamedEra without a beginning")
    @Test
    void shouldNotInitNamedEraWithoutBeginning() {
        assertThatIllegalStateException().isThrownBy(new NamedEraBuilder().withName("An era")::build)
                .withMessage("An era must have a beginning");
    }

    @DisplayName("should not build NamedEra when the end date is before the beginning")
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

    @DisplayName("should build a NamedEra when all required ")
    @Test
    void shouldInitValidInstanceOnlyApplyingNameAndBeginning() {
        assertThat(new NamedEraBuilder().withName("An era").withBeginning(LocalDate.now()).build()).isNotNull();
    }
}