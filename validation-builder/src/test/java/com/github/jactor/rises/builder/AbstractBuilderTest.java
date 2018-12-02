package com.github.jactor.rises.builder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.github.jactor.rises.builder.ValidationResult.validate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

@DisplayName("AbstractBuilder")
class AbstractBuilderTest {

    private AbstractBuilder<Bean> builder;

    @DisplayName("should build a bean")
    @Test void shouldBuildBean() {
        builder = new AbstractBuilder<Bean>(b -> Optional.empty()) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        Bean bean = builder.build();

        assertThat(bean).isNotNull();
    }

    @DisplayName("should fail the build when the instance is not valid")
    @Test void shouldFailValidationOfBean() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(validate(Bean.class).notNull("nullField", null, "cannot be null"))
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Bean has invalid fields:\n- 'nullField' cannot be null");
    }

    @DisplayName("should fail the build when the instance is not valid and provide the names of all invalid fields")
    @Test void shouldFailValidationOfBeanWithMessageContainingAllTheInvalidFields() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(validate(Bean.class)
                        .notNull("aField", null, "cannot be null")
                        .notNull("anotherField", null, "cannot be null")
                )
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Bean has invalid fields:\n- 'aField' cannot be null,\n- 'anotherField' cannot be null");
    }

    @DisplayName("should fail the build when a string is empty")
    @Test void shouldFailValidationOfBeanWhenStringIsEmpty() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(validate(Bean.class).notEmpty("emptyField", "", "cannot be empty"))
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Bean has invalid fields:\n- 'emptyField' cannot be empty");
    }

    @DisplayName("should fail the build when a condition is true")
    @Test void shouldFailValidationOfBeanWhenConditionIsTrue() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(validate(Bean.class).notTrue("fieldName", () -> true, "validation cannot be true"))
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Bean has invalid fields:\n- 'fieldName' validation cannot be true");
    }

    @DisplayName("should fail the build when a condition is false")
    @Test void shouldFailValidationOfBeanWhenConditionIsFalse() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(validate(Bean.class).notFalse("fieldName", () -> false, "validation cannot be false"))
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Bean has invalid fields:\n- 'fieldName' validation cannot be false");
    }

    private class Bean {
    }
}
