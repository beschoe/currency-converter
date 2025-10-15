package com.mercateo.common.i18n.units;

import lombok.NonNull;
import lombok.Value;

@Value
public class LocalizedUnitKey {
    private static final String DELIMITER = "!";

    @NonNull
    private String unit;

    @NonNull
    private String language;

    public String encode() {
        return this.unit + DELIMITER + this.language;
    }
}
