package com.github.jactorrises.builder;

import java.util.Optional;

/**
 * Every builder should provide an implementation of a valid instance...
 */
@FunctionalInterface
public interface ValidInstance<T> {
    /**
     * @param bean to fetch error message from when invalid
     * @return an {@link Optional} message if instance is not valid, {@link Optional#empty()} if valid
     */
    Optional<String> provideInvalidMessage(T bean);
}
