package com.mercateo.common.i18n.units;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class LocalizedUnitValueTest {
    @Test
    public void field_order_for_constructor() {
        LocalizedUnitValue uut = new LocalizedUnitValue("theSingular", "thePlural");

        assertThat(uut.getSingular()).isEqualTo("theSingular");
        assertThat(uut.getPlural()).isEqualTo("thePlural");
    }

    @Test
    public void decode() {
        LocalizedUnitValue uut = LocalizedUnitValue.decode("theSingular!thePlural");

        assertThat(uut).isEqualTo(new LocalizedUnitValue("theSingular", "thePlural"));
    }

    @Test
    public void decode_defensive() {
        assertThat(LocalizedUnitValue.decode("theSingular"))
                .isEqualTo(new LocalizedUnitValue("theSingular", ""));
        assertThat(LocalizedUnitValue.decode(""))
                .isEqualTo(new LocalizedUnitValue("", ""));
        assertThat(LocalizedUnitValue.decode(null))
                .isEqualTo(new LocalizedUnitValue("", ""));
        assertThat(LocalizedUnitValue.decode("a!b!c"))
                .isEqualTo(new LocalizedUnitValue("a", "b"));
    }
}
