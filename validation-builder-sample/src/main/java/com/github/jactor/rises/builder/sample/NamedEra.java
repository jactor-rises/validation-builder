package com.github.jactor.rises.builder.sample;

import com.github.jactor.rises.builder.ValidInstance;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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
        return NamedEra.class.getSimpleName() + " named " + name + ": from " + beginning + " to " + end;
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
        return namedEra -> {
            if (namedEra.getName() == null) {
                return Optional.of("A named era must have a name");
            }

            if (namedEra.getBeginning() == null) {
                return Optional.of(namedEra.getName() + " must have a beginning");
            }

            if (namedEra.getEnd() != null && namedEra.getEnd().isBefore(namedEra.getBeginning())) {
                return Optional.of(namedEra.getName() + " cannot end before it is started");
            }

            return Optional.empty();
        };
    }

}
