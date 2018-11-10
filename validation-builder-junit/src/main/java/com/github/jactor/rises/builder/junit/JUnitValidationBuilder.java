package com.github.jactor.rises.builder.junit;

import com.github.jactor.rises.builder.ValidInstance;
import com.github.jactor.rises.builder.ValidationBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.github.jactor.rises.builder.junit.JUnitValidationBuilder.SuppressValidation.NONE;
import static com.github.jactor.rises.builder.junit.JUnitValidationBuilder.SuppressValidation.ONE;
import static com.github.jactor.rises.builder.junit.JUnitValidationBuilder.SuppressValidation.SUBTRACT;

/**
 * A {@link ValidationBuilder} which applies conditional validations used for testing...
 */
public abstract class JUnitValidationBuilder extends ValidationBuilder<Object> {
    private static final Map<Class<?>, Integer> SUPPRESS_FOR_CLASS = new HashMap<>();

    private JUnitValidationBuilder() {
        super(null);
    }

    public static void useDefaultValidations() {
        ValidationBuilder.applyValidationRunner(new ValidationBuilder.ValidationRunner());
    }

    public static void suppressOneValidationFor(Class<?> aClass) {
        useConditionalValidation();
        SUPPRESS_FOR_CLASS.put(aClass, 1);
    }

    private static void useConditionalValidation() {
        ValidationBuilder.applyValidationRunner(new SuppressVelidationRunner());
    }

    public static void suppressValidation(Class<?> aClass, int numberOfTimes) {
        useConditionalValidation();
        SUPPRESS_FOR_CLASS.put(aClass, numberOfTimes);
    }

    static class SuppressVelidationRunner extends ValidationBuilder.ValidationRunner {
        @Override
        protected <V> Optional<String> run(ValidInstance<V> validInstance, V bean) {
            @SuppressWarnings("unchecked") Class clazz = bean.getClass();

            SuppressValidation suppress = SUPPRESS_FOR_CLASS.keySet().stream()
                    .filter(aClass -> aClass.equals(bean.getClass()))
                    .map(aClass -> SUPPRESS_FOR_CLASS.get(aClass) == 1 ? ONE : SUBTRACT)
                    .findFirst()
                    .orElse(NONE);

            switch (suppress) {
                case ONE:
                    SUPPRESS_FOR_CLASS.remove(clazz);
                    return Optional.empty();

                case SUBTRACT:
                    int noOfValidations = SUPPRESS_FOR_CLASS.get(clazz) - 1;
                    SUPPRESS_FOR_CLASS.put(clazz, noOfValidations);
                    return Optional.empty();

                default:
                    return super.run(validInstance, bean);
            }
        }
    }

    enum SuppressValidation {
        NONE, ONE, SUBTRACT
    }
}
