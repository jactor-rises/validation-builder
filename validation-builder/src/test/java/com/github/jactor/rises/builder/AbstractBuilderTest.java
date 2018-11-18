package com.github.jactor.rises.builder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

@DisplayName("AbstractBuilder")
class AbstractBuilderTest {

    private AbstractBuilder<Bean> builder;

    @DisplayName("should build a validated bean")
    @Test
    void shouldReturnValidatedBean() {
        builder = new AbstractBuilder<Bean>(b -> Optional.empty()) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        Bean bean = builder.build();

        assertThat(bean).isNotNull();
    }

    @DisplayName("should fail the build when the instance is not valid")
    @Test
    void shouldFailValidationOfBean() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(new InvalidFields().addWhenNull("fieldName", null))
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Bean has invalid field from build: fieldName");
    }

    @DisplayName("should fail the build when the instance is not valid and provide the names of all invalid fields")
    @Test
    void shouldFailValidationOfBeanWithMessageContainingAllTheInvalidFields() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(new InvalidFields()
                        .addWhenNull("fieldName", null)
                        .addWhenNull("anotherField", null)
                )
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Bean has invalid fields from build: fieldName, anotherField");
    }

    @DisplayName("should fail the build when a string is empty")
    @Test
    void shouldFailValidationOfBeanWhenStringIsEmpty() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(new InvalidFields().addWhenEmpty("fieldName", ""))
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Bean has invalid field from build: fieldName");
    }

    @DisplayName("should fail the build when a condition is true")
    @Test
    void shouldFailValidationOfBeanWhenConditionIsTrue() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(new InvalidFields().addWhenTrue("fieldName", () -> true))
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Bean has invalid field from build: fieldName");
    }

    @DisplayName("should fail the build when a condition is false")
    @Test
    void shouldFailValidationOfBeanWhenConditionIsFalse() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(new InvalidFields().addWhenFalse("fieldName", () -> false))
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Bean has invalid field from build: fieldName");
    }

    private class Bean {
    }
}
