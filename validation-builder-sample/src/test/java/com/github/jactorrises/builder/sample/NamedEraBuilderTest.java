package com.github.jactorrises.builder.sample;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NamedEraBuilderTest {

    @Test
    void shouldNotInitNamedEraWithoutName() {
        IllegalStateException e = assertThrows(
                IllegalStateException.class, () -> new NamedEraBuilder().build()
        );

        assertThat(e.getMessage(), equalTo("A named era must have a name"));
    }

    @Test
    void shouldNotInitNamedAreatWithoutBeginning() {
        IllegalStateException e = assertThrows(
                IllegalStateException.class, () -> new NamedEraBuilder().withName("An era").build()
        );

        assertThat(e.getMessage(), equalTo("An era must have a beginning"));
    }

    @Test
    void shouldNotInitNamedAreatWithBeginningAfterTheEnd() {
        IllegalStateException e = assertThrows(
                IllegalStateException.class, () -> new NamedEraBuilder()
                        .withName("An era")
                        .withBeginning(LocalDate.now())
                        .withEnd(LocalDate.now().minusDays(1))
                        .build()
        );

        assertThat(e.getMessage(), equalTo("An era cannot end before it is started"));
    }

    @Test
    void shouldInitValidInstanceOnlyApplyingNameAndBeginning() {
        assertNotNull(new NamedEraBuilder().withName("An era").withBeginning(LocalDate.now()).build());
    }
}