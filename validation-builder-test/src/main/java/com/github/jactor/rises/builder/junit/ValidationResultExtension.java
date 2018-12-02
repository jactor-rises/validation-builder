package com.github.jactor.rises.builder.junit;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public final class ValidationResultExtension implements BeforeEachCallback, AfterEachCallback {

    private ValidationResultExtension() {
    }

    @Override public void afterEach(ExtensionContext context) {
        SuppressValidationResult.reset();
    }

    @Override public void beforeEach(ExtensionContext context) {
        SuppressValidationResult.refresh();
    }

    public static void suppressValidationFor(Class<?> aClass) {
        SuppressValidationResult.suppressValidationFor(aClass, Integer.MAX_VALUE);
    }

    public static void suppressValidationFor(Class<?> aClass, int numberOfTimes) {
        SuppressValidationResult.suppressValidationFor(aClass, numberOfTimes);
    }
}
