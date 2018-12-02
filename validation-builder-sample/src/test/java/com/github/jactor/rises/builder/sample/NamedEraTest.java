package com.github.jactor.rises.builder.sample;

import com.github.jactor.rises.builder.junit.ValidationResultExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NamedEra")
@ExtendWith(ValidationResultExtension.class)
class NamedEraTest {

    @DisplayName("should calculate number of months in an era")
    @Test void shouldCalculateNoOfMonthsInAnEra() {
        NamedEra namedEra = NamedEra.aNamedEra()
                .withBeginning(LocalDate.now().minusYears(3))
                .withEnd(LocalDate.now().minusYears(1))
                .build();

        assertThat(namedEra.calculateLength(ChronoUnit.MONTHS)).isEqualTo(24L);
    }

    @DisplayName("should calculate length of era against todays date when no end date is not specified")
    @Test void shouldCalculateEraLenghtUsingToDaysDateWhenNoEndDateIsSpecified() {
        NamedEra namedEra = NamedEra.aNamedEra()
                .withBeginning(LocalDate.now().minusYears(1))
                .build();

        assertThat(namedEra.calculateLength(ChronoUnit.MONTHS)).isEqualTo(12L);
    }
}
