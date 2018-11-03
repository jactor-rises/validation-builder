package com.github.jactorrises.builder;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class ValidationBuilderTest {

    private ValidationBuilder<Bean> validationBuilder;

    @Test
    void shouldReturnValidatedBean() {
        validationBuilder = new TestValidationBuilder(bean -> Optional.empty());
        Bean bean = validationBuilder.build();

        assertThat(bean).isNotNull();
    }

    @Test
    void shouldFailValidationOfBean() {
        validationBuilder = new TestValidationBuilder(bean -> Optional.of("invalid"));

        assertThatIllegalStateException().isThrownBy(() -> validationBuilder.build())
                .withMessage("invalid");
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
