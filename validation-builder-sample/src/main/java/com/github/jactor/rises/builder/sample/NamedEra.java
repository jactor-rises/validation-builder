package com.github.jactor.rises.builder.sample;

import com.github.jactor.rises.builder.ValidInstance;
import com.github.jactor.rises.builder.ValidationResult;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class NamedEra {
    private LocalDate beginning;
    private LocalDate end;
    private String name;

    public long calculateLength(ChronoUnit chronoUnit) {
        LocalDate eraEnd = end != null ? end : LocalDate.now();
        return chronoUnit.between(beginning, eraEnd);
    }

    @Override
    public String toString() {
        return String.format("%s named %s from %s%s", NamedEra.class.getSimpleName(), name, beginning, end != null ? " to " + end : "");
    }

    public LocalDate getBeginning() {
        return beginning;
    }

    public LocalDate getEnd() {
        return end;
    }

    public String getName() {
        return name;
    }

    void setBeginning(LocalDate beginning) {
        this.beginning = beginning;
    }

    void setEnd(LocalDate end) {
        this.end = end;
    }

    void setName(String name) {
        this.name = name;
    }

    public static NamedEraBuilder aNamedEra() {
        return new NamedEraBuilder(validate());
    }

    static ValidInstance<NamedEra> validate() {
        return namedEra -> ValidationResult.validate(NamedEra.class)
                .notEmpty("name", namedEra.getName(), "must be named")
                .notNull("beginning", namedEra.getBeginning(), "must have a beginning")
                .notTrue("end", () -> namedEra.getEnd() != null && namedEra.getEnd().isEqual(namedEra.getBeginning()), "cannot be equal to the beginning")
                .notTrue("end", () -> namedEra.getEnd() != null && namedEra.getEnd().isBefore(namedEra.getBeginning()), "cannot come before the beginning")
                .returnResult();
    }
}
