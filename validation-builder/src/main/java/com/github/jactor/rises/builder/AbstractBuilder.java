package com.github.jactor.rises.builder;

import java.util.Optional;

/**
 * A builder which does not return a bean instance before its state is validated using the the {@link FunctionalInterface}
 * called {@link ValidInstance}
 *
 * @param <T> type of bean to build
 */
public abstract class AbstractBuilder<T> {
    private static ValidationRunner validationRunner;
    private final ValidInstance<T> validInstance;

    protected AbstractBuilder(ValidInstance<T> validInstance) {
        this.validInstance = validInstance;
    }

    protected abstract T buildBean();

    public T build() {
        T bean = buildBean();
        Optional<String> invalidMessage = validationRunner.run(validInstance, bean);

        if (invalidMessage.isPresent()) {
            throw new IllegalStateException(invalidMessage.get());
        }

        return bean;
    }

    protected static void applyValidationRunner(ValidationRunner validationRunner) {
        AbstractBuilder.validationRunner = validationRunner;
    }

    public static class ValidationRunner {
        protected <V> Optional<String> run(ValidInstance<V> validInstance, V bean) {
            return validInstance.provideInvalidMessage(bean);
        }
    }

    static {
        applyValidationRunner(new ValidationRunner());
    }
}
