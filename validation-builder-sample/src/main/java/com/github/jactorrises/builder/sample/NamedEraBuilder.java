package com.github.jactorrises.builder.sample;

import com.github.jactorrises.builder.ValidationBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class NamedEraBuilder extends ValidationBuilder<NamedEra> {
    private LocalDate beginning;
    private LocalDate end;
    private String name;

    NamedEraBuilder() {
        super(namedEra -> {
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
        });
    }

    @Override
    protected NamedEra buildBean() {
        NamedEra namedEra = new NamedEra();
        namedEra.setBeginning(beginning);
        namedEra.setEnd(end);
        namedEra.setName(name);

        return namedEra;
    }

    public NamedEraBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public NamedEraBuilder withBeginning(LocalDate beginning) {
        this.beginning = beginning;
        return this;
    }

    public NamedEraBuilder withEnd(LocalDate end) {
        this.end = end;
        return this;
    }
}
