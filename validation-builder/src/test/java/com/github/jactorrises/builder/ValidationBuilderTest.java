package com.github.jactorrises.builder;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationBuilderTest {

    private ValidationBuilder<Bean> validationBuilder;

    @Test
    void shouldReturnValidatedBean() {
        validationBuilder = new TestValidationBuilder(bean -> Optional.empty());
        Bean bean = validationBuilder.build();

        assertThat(bean, is(notNullValue()));
    }

    @Test
    void shouldFailValidationOfBean() {
        validationBuilder = new TestValidationBuilder(bean -> Optional.of("invalid"));

        IllegalStateException iae = assertThrows(IllegalStateException.class, () -> validationBuilder.build());

        assertThat(iae.getMessage(), equalTo("invalid"));
    }

    class Bean {
    }

    private class TestValidationBuilder extends ValidationBuilder<Bean> {

        private TestValidationBuilder(ValidInstance<Bean> validInstance) {
            super(validInstance);
        }

        @Override
        protected Bean buildBean() {
            return new Bean();
        }
    }
}
