package com.github.jactor.rises.builder.junit;

import com.github.jactor.rises.builder.AbstractBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("The JunitValidationBuilder")
class JUnitBuilderTest {

    @DisplayName("should suppress build validation")
    @Test
    void shouldSuppressBuildValidation() {
        assertAll(
                () -> assertThrows(IllegalStateException.class, new InvalidBeanBuilder()::build),
                () -> {
                    JUnitBuilder.suppressOneValidationFor(InvalidBean.class);
                    assertThat(new InvalidBeanBuilder().build()).isNotNull();
                }
        );
    }

    @DisplayName("should suppress build validation for given class only")
    @Test
    void shouldSuppressBuildValidationOnlyForGivenClass() {
        JUnitBuilder.suppressOneValidationFor(InvalidBean.class);

        assertAll(
                () -> assertThrows(IllegalStateException.class, new AnotherInvalidBeanBuilder()::build),
                () -> assertThat(new InvalidBeanBuilder().build()).isNotNull()
        );
    }

    @DisplayName("should suppress build validation only a given number of times")
    @Test
    void shouldSuppressBuildValidationGivenNumberOfTimes() {
        JUnitBuilder.suppressValidation(InvalidBean.class, 2);

        assertAll(
                () -> assertThat(new InvalidBeanBuilder().build()).isNotNull(),
                () -> assertThat(new InvalidBeanBuilder().build()).isNotNull(),
                () -> assertThrows(IllegalStateException.class, new InvalidBeanBuilder()::build)
        );
    }

    private class InvalidBean {

    }

    private class AnotherInvalidBean {

    }

    private class InvalidBeanBuilder extends AbstractBuilder<InvalidBean> {
        InvalidBeanBuilder() {
            super(validBean -> Optional.of("always an invalid bean"));
        }

        @Override
        protected InvalidBean buildBean() {
            return new InvalidBean();
        }
    }

    private class AnotherInvalidBeanBuilder extends AbstractBuilder<AnotherInvalidBean> {
        AnotherInvalidBeanBuilder() {
            super(validBean -> Optional.of("always an invalid bean"));
        }

        @Override
        protected AnotherInvalidBean buildBean() {
            return new AnotherInvalidBean();
        }
    }
}