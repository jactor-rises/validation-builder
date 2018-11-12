package com.github.jactor.rises.builder.sample;

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
        return new NamedEraBuilder();
    }
}
