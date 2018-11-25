package com.github.jactor.rises.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ValidationResult {
    protected static ValidationResult instance = new ValidationResult();

    private final List<String> names = new ArrayList<>();

    protected ValidationResult() {
    }

    public ValidationResult notNull(String fieldName, Object fieldValue) {
        if (fieldValue == null) {
            names.add(fieldName);
        }

        return this;
    }

    public ValidationResult notEmpty(String fieldName, String fieldValue) {
        if (fieldValue == null || "".equals(fieldValue.trim())) {
            names.add(fieldName);
        }

        return this;
    }

    public ValidationResult notTrue(String fieldName, FieldCondition fieldCondition) {
        if (fieldCondition.isTrue()) {
            names.add(fieldName);
        }

        return this;
    }

    public ValidationResult notFalse(String fieldName, FieldCondition fieldCondition) {
        if (!fieldCondition.isTrue()) {
            names.add(fieldName);
        }

        return this;
    }

    public static void reset() {
        instance = new ValidationResult();
    }

    protected void throwWhenInvalid(ValidationResult validationResult, Class<?> beanClass) {
        String fields = validationResult.names.size() == 1 ? "field" : "fields";

        throw new IllegalStateException(
                String.format("%s has invalid %s from build: %s", beanClass.getSimpleName(), fields, String.join(", ", validationResult.names))
        );
    }

    static void throwIllegalStateException(ValidationResult validationResult, Class<?> beanClass) {
        instance.throwWhenInvalid(validationResult, beanClass);
    }

    public Optional<ValidationResult> provideWhenInvalid() {
        return names.isEmpty() ? Optional.empty() : Optional.of(this);
    }

    public static ValidationResult validate() {
        return new ValidationResult();
    }

    @FunctionalInterface
    public interface FieldCondition {

        boolean isTrue();
    }
}
