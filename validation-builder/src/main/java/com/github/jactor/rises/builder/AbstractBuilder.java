package com.github.jactor.rises.builder;

/**
 * A builder which does not return a bean instance before its state is validated using a {@link FunctionalInterface}
 * called {@link ValidInstance}
 *
 * @param <T> type of bean to build
 */
public abstract class AbstractBuilder<T> {
    private final ValidInstance<T> validInstance;

    protected AbstractBuilder(ValidInstance<T> validInstance) {
        this.validInstance = validInstance;
    }

    protected abstract T buildBean();

    public T build() {
        T bean = buildBean();

        validInstance.validate(bean)
                .ifPresent(ValidationResult::throwIllegalStateExceptionWhenInvalid);

        return bean;
    }
}
