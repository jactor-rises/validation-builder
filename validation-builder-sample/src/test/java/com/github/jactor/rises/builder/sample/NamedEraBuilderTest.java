package com.github.jactor.rises.builder.sample;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

@DisplayName("NamedEraBuilder")
class NamedEraBuilderTest {

    @DisplayName("should not build NamedEra without a naming it")
    @Test void shouldNotInitNamedEraWithoutName() {
        assertThatIllegalStateException().isThrownBy(new NamedEraBuilder(NamedEra.validate())::build)
                .withMessageContaining("must be named");
    }

    @DisplayName("should not build NamedEra without a beginning")
    @Test void shouldNotInitNamedEraWithoutBeginning() {
        assertThatIllegalStateException().isThrownBy(new NamedEraBuilder(NamedEra.validate()).withName("An era")::build)
                .withMessageContaining("must have a beginning");
    }

    @DisplayName("should not build NamedEra when the end date is before the beginning")
    @Test void shouldNotInitNamedAreatWithBeginningAfterTheEnd() {
        assertThatIllegalStateException().isThrownBy(() ->
                new NamedEraBuilder(NamedEra.validate())
                        .withName("The era")
                        .withBeginning(LocalDate.now())
                        .withEnd(LocalDate.now().minusDays(1))
                        .build()
        ).withMessageContaining("'end' cannot come before the beginning");
    }

    @DisplayName("should not build NamedEra when the end date is equal to the beginning")
    @Test void shouldNotInitNamedAreatWithBeginningEqualToTheEnd() {
        assertThatIllegalStateException().isThrownBy(() ->
                new NamedEraBuilder(NamedEra.validate())
                        .withName("The era")
                        .withBeginning(LocalDate.now())
                        .withEnd(LocalDate.now())
                        .build()
        ).withMessageContaining("'end' cannot be equal to the beginning");
    }

    @DisplayName("should build a NamedEra when all required ")
    @Test void shouldInitValidInstanceOnlyApplyingNameAndBeginning() {
        assertThat(
                new NamedEraBuilder(NamedEra.validate()).withName("An era").withBeginning(LocalDate.now()).build()
        ).isNotNull();
    }
}