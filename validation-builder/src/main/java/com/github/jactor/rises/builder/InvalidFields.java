package com.github.jactor.rises.builder;

import java.util.ArrayList;
import java.util.List;

public class InvalidFields {
    private final List<String> names = new ArrayList<>();

    public InvalidFields addWhenNull(String fieldName, Object fieldValue) {
        if (fieldValue == null) {
            names.add(fieldName);
        }

        return this;
    }

    public InvalidFields addWhenEmpty(String fieldName, String fieldValue) {
        if (fieldValue == null || "".equals(fieldValue.trim())) {
            names.add(fieldName);
        }

        return this;
    }

    public InvalidFields addWhenTrue(String fieldName, FieldCondition fieldCondition) {
        if (fieldCondition.isTrue()) {
            names.add(fieldName);
        }

        return this;
    }

    public InvalidFields addWhenFalse(String fieldName, FieldCondition fieldCondition) {
        if (!fieldCondition.isTrue()) {
            names.add(fieldName);
        }

        return this;
    }

    static void throwIllegalStateException(InvalidFields invalidFields, Class<?> beanClass) {
        String fields = invalidFields.names.size() == 1 ? "field" : "fields";

        throw new IllegalStateException(
                String.format("%s has invalid %s from build: %s", beanClass.getSimpleName(), fields, String.join(", ", invalidFields.names))
        );
    }

    @FunctionalInterface
    public interface FieldCondition {
        boolean isTrue();
    }
}
