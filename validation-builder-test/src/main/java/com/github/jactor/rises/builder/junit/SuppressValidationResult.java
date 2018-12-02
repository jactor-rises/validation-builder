package com.github.jactor.rises.builder.junit;

import com.github.jactor.rises.builder.ValidationResult;

import java.util.HashMap;
import java.util.Map;

class SuppressValidationResult extends ValidationResult {
    private static final Map<Class<?>, Integer> SUPPRESS_FOR_CLASS = new HashMap<>();

    @Override protected void throwIllegalStateExceptionWhenInvalid() {
        if (SUPPRESS_FOR_CLASS.isEmpty()) {
            return;
        }

        if (SUPPRESS_FOR_CLASS.containsKey(getClassToValidate())) {
            Integer noOfSuppressedValidations = SUPPRESS_FOR_CLASS.get(getClassToValidate());

            if (0 < noOfSuppressedValidations) {
                SUPPRESS_FOR_CLASS.put(getClassToValidate(), --noOfSuppressedValidations);
                return;
            }
        }

        super.throwIllegalStateExceptionWhenInvalid();
    }

    static void refresh() {
        ValidationResult.setNewInstance(SuppressValidationResult::new);
        SUPPRESS_FOR_CLASS.clear();
    }

    static void suppressValidationFor(Class<?> aClass, int numberOfTimes) {
        SUPPRESS_FOR_CLASS.put(aClass, numberOfTimes);
    }
}
