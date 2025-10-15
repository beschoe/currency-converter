package com.mercateo.common.i18n;

import java.util.ArrayList;
import java.util.List;

import com.mercateo.common.util.annotations.NonNull;
import com.mercateo.common.util.annotations.NonNullByDefault;

@NonNullByDefault
public class ImpressumBuilder {
    private List<String> lines = new ArrayList<>();

    public ImpressumBuilder addImpressumLine(@NonNull String line) {
        lines.add(line);
        return this;
    }

    public Impressum build() {
        return new Impressum(lines);
    }

    enum LineSeparator {
        HTML_BR, PLAIN_BL_N
    }

}