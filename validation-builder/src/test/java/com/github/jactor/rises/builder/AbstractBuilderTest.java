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
                .withMessage("Invalid field from build: fieldName");
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
                .withMessage("Invalid fields from build: fieldName, anotherField");
    }

    private class Bean {
    }
}
