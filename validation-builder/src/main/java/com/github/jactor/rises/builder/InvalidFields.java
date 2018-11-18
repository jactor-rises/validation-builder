package com.github.jactor.rises.builder;

import java.util.ArrayList;
import java.util.List;

public class InvalidFields {
    private final List<String> invalidFieldNames = new ArrayList<>();

    void throwIllegalStateException() {
        String fields = invalidFieldNames.size() == 1 ? "field" : "fields";

        throw new IllegalStateException(
                String.format("Invalid %s from build: %s", fields, String.join(", ", invalidFieldNames))
        );
    }

    public InvalidFields addWhenNull(String fieldName, Object fieldValue) {
        if (fieldValue == null) {
            invalidFieldNames.add(fieldName);
        }

        return this;
    }
}
