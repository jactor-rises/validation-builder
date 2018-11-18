package com.github.jactor.rises.builder;

import java.util.Optional;

/**
 * Every builder should be able to validate the bean it is building
 */
@FunctionalInterface
public interface ValidInstance<T> {
    /**
     * @param bean to validate
     * @return an {@link Optional} with {@link InvalidFields} if instance is not valid, {@link Optional#empty()} if valid
     */
    Optional<InvalidFields> validate(T bean);
}
