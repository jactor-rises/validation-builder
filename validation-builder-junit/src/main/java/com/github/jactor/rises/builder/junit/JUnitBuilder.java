package com.github.jactor.rises.builder.junit;

import com.github.jactor.rises.builder.ValidInstance;
import com.github.jactor.rises.builder.AbstractBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.github.jactor.rises.builder.junit.JUnitBuilder.SuppressValidation.NONE;
import static com.github.jactor.rises.builder.junit.JUnitBuilder.SuppressValidation.ONE;
import static com.github.jactor.rises.builder.junit.JUnitBuilder.SuppressValidation.SUBTRACT;

/**
 * A {@link AbstractBuilder} which applies conditional validations used for testing...
 */
public abstract class JUnitBuilder extends AbstractBuilder<Object> {
    private static final Map<Class<?>, Integer> SUPPRESS_FOR_CLASS = new HashMap<>();

    private JUnitBuilder() {
        super(null);
    }

    public static void useDefaultValidations() {
        AbstractBuilder.applyValidationRunner(new AbstractBuilder.ValidationRunner());
    }

    public static void suppressOneValidationFor(Class<?> aClass) {
        useConditionalValidation();
        SUPPRESS_FOR_CLASS.put(aClass, 1);
    }

    private static void useConditionalValidation() {
        AbstractBuilder.applyValidationRunner(new SuppressVelidationRunner());
    }

    public static void suppressValidation(Class<?> aClass, int numberOfTimes) {
        useConditionalValidation();
        SUPPRESS_FOR_CLASS.put(aClass, numberOfTimes);
    }

    static class SuppressVelidationRunner extends AbstractBuilder.ValidationRunner {
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
