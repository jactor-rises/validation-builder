package com.github.jactorrises.builder.sample;

import com.github.jactorrises.builder.junit.JUnitValidationBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class NamedEraTest {
    @Test
    void shouldCalculateNoOfMonthsInAnEra() {
        JUnitValidationBuilder.suppressOneValidationFor(NamedEra.class);
        NamedEra namedEra = NamedEra.init()
                .withBeginning(LocalDate.now().minusYears(3))
                .withEnd(LocalDate.now().minusYears(1))
                .build();

        assertThat(namedEra.calculateLength(ChronoUnit.MONTHS)).isEqualTo(24L);
    }

    @Test
    void shouldCalculateEraLenghtUsingToDaysDateWhenNoEndDateIsSpecified() {
        JUnitValidationBuilder.suppressOneValidationFor(NamedEra.class);
        NamedEra namedEra = NamedEra.init()
                .withBeginning(LocalDate.now().minusYears(1))
                .build();

        assertThat(namedEra.calculateLength(ChronoUnit.MONTHS)).isEqualTo(12L);
    }
}
