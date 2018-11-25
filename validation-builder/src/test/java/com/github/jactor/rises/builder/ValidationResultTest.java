package com.github.jactor.rises.builder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("ValidationResult")
class ValidationResultTest {

    private ValidationResult validationResult = ValidationResult.validate();

    @DisplayName("should throw exception when there are fields that are not valid")
    @Test void shouldThrowExceptionWhenThereAreFieldsThatAreNotValid() {
        validationResult.notNull("nullField", null);

        assertAll(
                () -> assertThatIllegalStateException().as("the field should be null")
                        .isThrownBy(() -> ValidationResult.throwIllegalStateException(validationResult, ValidationResultTest.class))
                        .withMessage("ValidationResultTest has invalid field from build: nullField"),
                () -> {
                    validationResult.notTrue("trueField", () -> true)
                            .notFalse("falseField", () -> false)
                            .notEmpty("emptyField", "");

                    assertThatIllegalStateException().as("four fields should not be valid")
                            .isThrownBy(() -> ValidationResult.throwIllegalStateException(validationResult, ValidationResultTest.class))
                            .withMessage("ValidationResultTest has invalid fields from build: nullField, trueField, falseField, emptyField");
                }
        );
    }
}