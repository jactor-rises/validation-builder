package com.github.jactorrises.builder.junit;

import com.github.jactorrises.builder.ValidationBuilder;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JUnitValidationBuilderTest {

    @Test
    void shouldSuppressBuildValidation() {
        assertAll(
                () -> assertThrows(IllegalStateException.class, () -> new InvalidBeanBuilder().build()),
                () -> {
                    JUnitValidationBuilder.suppressOneValidationFor(InvalidBean.class);
                    assertThat(new InvalidBeanBuilder().build(), is(notNullValue()));
                }
        );
    }

    @Test
    void shouldSuppressBuildValidationOnlyForGivenClass() {
        JUnitValidationBuilder.suppressOneValidationFor(InvalidBean.class);

        assertAll(
                () -> assertThrows(IllegalStateException.class, () -> new AnotherInvalidBeanBuilder().build()),
                () -> assertThat(new InvalidBeanBuilder().build(), is(notNullValue()))
        );
    }

    @Test
    void shouldSuppressBuildValidationGivenNumberOfTimes() {
        JUnitValidationBuilder.suppressValidation(InvalidBean.class, 2);

        assertAll(
                () -> assertThat(new InvalidBeanBuilder().build(), is(notNullValue())),
                () -> assertThat(new InvalidBeanBuilder().build(), is(notNullValue())),
                () -> assertThrows(IllegalStateException.class, () -> new InvalidBeanBuilder().build())
        );
    }

    private class InvalidBean {

    }

    private class AnotherInvalidBean {

    }

    private class InvalidBeanBuilder extends ValidationBuilder<InvalidBean> {
        InvalidBeanBuilder() {
            super(validBean -> Optional.of("always an invalid bean"));
        }

        @Override
        protected InvalidBean buildBean() {
            return new InvalidBean();
        }
    }

    private class AnotherInvalidBeanBuilder extends ValidationBuilder<AnotherInvalidBean> {
        AnotherInvalidBeanBuilder() {
            super(validBean -> Optional.of("always an invalid bean"));
        }

        @Override
        protected AnotherInvalidBean buildBean() {
            return new AnotherInvalidBean();
        }
    }
}