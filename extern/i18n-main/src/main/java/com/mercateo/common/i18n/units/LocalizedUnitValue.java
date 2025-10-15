package com.mercateo.common.i18n.units;

import com.mercateo.common.util.annotations.Nullable;

import lombok.NonNull;
import lombok.Value;

@Value
public class LocalizedUnitValue {
    private static final LocalizedUnitValue EMPTY = new LocalizedUnitValue("", "");

    @NonNull
    private String singular;

    @NonNull
    private String plural;

    public static LocalizedUnitValue decode(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return EMPTY;
        }
        String[] split = value.split("!");
        if (split.length >= 2) {
            // more than one '!' -> take only the first two values from the
            // split
            return new LocalizedUnitValue(split[0], split[1]);
        }
        // no '!' -> take first as singular and leave plural empty
        return new LocalizedUnitValue(split[0], "");
    }
}
