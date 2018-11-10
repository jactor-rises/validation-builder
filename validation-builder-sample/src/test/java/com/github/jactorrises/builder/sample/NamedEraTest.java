package com.github.jactorrises.builder.sample;

import com.github.jactorrises.builder.junit.JUnitValidationBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NamedEra")
class NamedEraTest {

    @DisplayName("should calculate number of months in an era")
    @Test
    void shouldCalculateNoOfMonthsInAnEra() {
        JUnitValidationBuilder.suppressOneValidationFor(NamedEra.class);
        NamedEra namedEra = NamedEra.init()
                .withBeginning(LocalDate.now().minusYears(3))
                .withEnd(LocalDate.now().minusYears(1))
                .build();

        assertThat(namedEra.calculateLength(ChronoUnit.MONTHS)).isEqualTo(24L);
    }

    @DisplayName("should calculate length of era against todays date when no end date is not specified")
    @Test
    void shouldCalculateEraLenghtUsingToDaysDateWhenNoEndDateIsSpecified() {
        JUnitValidationBuilder.suppressOneValidationFor(NamedEra.class);
        NamedEra namedEra = NamedEra.init()
                .withBeginning(LocalDate.now().minusYears(1))
                .build();

        assertThat(namedEra.calculateLength(ChronoUnit.MONTHS)).isEqualTo(12L);
    }
}
