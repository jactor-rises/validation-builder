package com.github.jactor.rises.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ValidationResult {
    private static NewInstance newInstance = ValidationResult::new;

    private final List<String> names = new ArrayList<>();
    private Class<?> classToValidate;

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

    public Optional<ValidationResult> returnResult() {
        return names.isEmpty() ? Optional.empty() : Optional.of(this);
    }

    protected void throwIllegalStateExceptionWhenInvalid() {
        String fields = names.size() == 1 ? "field" : "fields";

        throw new IllegalStateException(
                String.format("%s has invalid %s from build: %s", getClassToValidate().getSimpleName(), fields, String.join(", ", names))
        );
    }

    protected Class<?> getClassToValidate() {
        return classToValidate;
    }

    private void setClassToValidate(Class<?> classToValidate) {
        this.classToValidate = classToValidate;
    }

    public static void reset() {
        newInstance = ValidationResult::new;
    }

    public static ValidationResult validate(Class<?> clazz) {
        ValidationResult validationResult = newInstance.init();
        validationResult.setClassToValidate(clazz);

        return validationResult;
    }

    @FunctionalInterface
    protected interface NewInstance {
        ValidationResult init();
    }

    @FunctionalInterface
    public interface FieldCondition {
        boolean isTrue();
    }
}
