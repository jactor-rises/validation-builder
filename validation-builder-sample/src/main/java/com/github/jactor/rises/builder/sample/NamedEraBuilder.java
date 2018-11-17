package com.github.jactor.rises.builder.sample;

import com.github.jactor.rises.builder.AbstractBuilder;
import com.github.jactor.rises.builder.ValidInstance;

import java.time.LocalDate;

public class NamedEraBuilder extends AbstractBuilder<NamedEra> {
    private LocalDate beginning;
    private LocalDate end;
    private String name;

    NamedEraBuilder(ValidInstance<NamedEra> validInstance) {
        super(validInstance);
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
