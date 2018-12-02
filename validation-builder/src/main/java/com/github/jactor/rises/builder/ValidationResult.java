package com.github.jactor.rises.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ValidationResult {
    private static NewInstance newInstance = ValidationResult::new;

    private final List<InvalidField> names = new ArrayList<>();
    private Class<?> classToValidate;

    protected ValidationResult() {
    }

    public ValidationResult notNull(String fieldName, Object fieldValue, String fieldMessage) {
        if (fieldValue == null) {
            names.add(new InvalidField(fieldName, fieldMessage));
        }

        return this;
    }

    public ValidationResult notEmpty(String fieldName, String fieldValue, String fieldMessage) {
        if (fieldValue == null || "".equals(fieldValue.trim())) {
            names.add(new InvalidField(fieldName, fieldMessage));
        }

        return this;
    }

    public ValidationResult notTrue(String fieldName, FieldCondition fieldCondition, String fieldMessage) {
        if (fieldCondition.isTrue()) {
            names.add(new InvalidField(fieldName, fieldMessage));
        }

        return this;
    }

    public ValidationResult notFalse(String fieldName, FieldCondition fieldCondition, String fieldMessage) {
        if (!fieldCondition.isTrue()) {
            names.add(new InvalidField(fieldName, fieldMessage));
        }

        return this;
    }

    public Optional<ValidationResult> returnResult() {
        return names.isEmpty() ? Optional.empty() : Optional.of(this);
    }

    protected void throwIllegalStateExceptionWhenInvalid() {
        throw new IllegalStateException(
                String.format("%s has invalid fields:%n- %s", getClassToValidate().getSimpleName(),
                        names.stream().map(Object::toString).collect(Collectors.joining(String.format(",%n- ")))
                )
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

    protected static void setNewInstance(NewInstance newInstance) {
        ValidationResult.newInstance = newInstance;
    }

    @FunctionalInterface
    protected interface NewInstance {
        ValidationResult init();
    }

    @FunctionalInterface
    public interface FieldCondition {
        boolean isTrue();
    }

    class InvalidField {
        final String fieldName;
        final String fieldMessage;

        InvalidField(String fieldName, String fieldMessage) {
            this.fieldName = fieldName;
            this.fieldMessage = fieldMessage;
        }

        @Override public String toString() {
            return "'" + fieldName + "' " + fieldMessage;
        }
    }
}
