package com.github.jactor.rises.builder.junit;

import com.github.jactor.rises.builder.AbstractBuilder;
import com.github.jactor.rises.builder.ValidationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("The ValidationResultExtension")
class ValidationResultExtensionTest {

    @ExtendWith(ValidationResultExtension.class)
    @DisplayName("used only as an declarative extension")
    @Nested
    class DeclarativeUse {

        @DisplayName("should suppress all build validations when no specification is given")
        @Test void shouldSuppressBuildValidations() {
            assertAll(
                    () -> assertThat(new InvalidBeanBuilder().build()).as("invalid bean").isNotNull(),
                    () -> assertThat(new AnotherInvalidBeanBuilder().build()).as("another invalid bean").isNotNull()
            );
        }
    }

    @ExtendWith(ValidationResultExtension.class)
    @DisplayName("used as an declarative, but programmatically extension")
    @Nested
    class ProgrammaticallyUse {

        @DisplayName("should suppress a validation for given class only")
        @Test void shouldSuppressBuildValidationOnlyForGivenClass() {
            ValidationResultExtension.suppressValidationFor(InvalidBean.class);

            assertAll(
                    () -> assertThat(new InvalidBeanBuilder().build()).as("invalid bean").isNotNull(),
                    () -> assertThatIllegalStateException().as("another invalid bean").isThrownBy(new AnotherInvalidBeanBuilder()::build)
            );
        }

        @DisplayName("should suppress build validation only a given number of times")
        @Test void shouldSuppressBuildValidationGivenNumberOfTimes() {
            ValidationResultExtension.suppressValidationFor(InvalidBean.class, 2);

            assertAll(
                    () -> assertThat(new InvalidBeanBuilder().build()).isNotNull(),
                    () -> assertThat(new InvalidBeanBuilder().build()).isNotNull(),
                    () -> assertThrows(IllegalStateException.class, new InvalidBeanBuilder()::build)
            );
        }
    }

    private class InvalidBean {
    }

    private class AnotherInvalidBean {
    }

    class InvalidBeanBuilder extends AbstractBuilder<InvalidBean> {
        InvalidBeanBuilder() {
            super(validInstance -> ValidationResult.validate(InvalidBean.class).notFalse("aField", () -> false, "validation cannot be false").returnResult());
        }

        @Override
        protected InvalidBean buildBean() {
            return new InvalidBean();
        }
    }

    class AnotherInvalidBeanBuilder extends AbstractBuilder<AnotherInvalidBean> {
        AnotherInvalidBeanBuilder() {
            super(validInstance -> ValidationResult.validate(AnotherInvalidBean.class).notFalse("aField", () -> false, "validation cannot be falseddddddddsdfadfasdfasdf").returnResult());
        }

        @Override
        protected AnotherInvalidBean buildBean() {
            return new AnotherInvalidBean();
        }
    }
}
