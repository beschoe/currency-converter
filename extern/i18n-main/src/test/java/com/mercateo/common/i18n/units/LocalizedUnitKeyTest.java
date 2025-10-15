package com.mercateo.common.i18n.units;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class LocalizedUnitKeyTest {
    @Test
    public void field_order_for_constructor() {
        LocalizedUnitKey uut = new LocalizedUnitKey("theUnit", "theLanguage");

        assertThat(uut.getUnit()).isEqualTo("theUnit");
        assertThat(uut.getLanguage()).isEqualTo("theLanguage");
    }

    @Test
    public void encode() {
        LocalizedUnitKey uut = new LocalizedUnitKey("theUnit", "theLanguage");

        assertThat(uut.encode()).isEqualTo("theUnit!theLanguage");
    }
}
