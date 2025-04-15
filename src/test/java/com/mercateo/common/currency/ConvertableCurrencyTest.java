package com.mercateo.common.currency;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.RoundingMode;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.mercateo.common.i18n.KnownCurrencies;

public class ConvertableCurrencyTest {
    @Test
    public void hasElementsMatching_i18n_ConvertableCurrency() throws Exception {
        assertThat(Stream.of(ConvertableCurrency.values()).map(ConvertableCurrency::name))
        .containsExactlyElementsOf(Stream.of(KnownCurrencies.values())
                .map(KnownCurrencies::name).collect(Collectors.toList()));
    }

    @Test
    public void defaultScales() {
        assertThat(ConvertableCurrency.HUF.getDefaultScale()).isEqualTo(0);
        assertThat(ConvertableCurrency.USD.getDefaultScale()).isEqualTo(2);
    }

    @Test
    public void defaultRoundingModes() {
        assertThat(ConvertableCurrency.HUF.getRoundingMode()).isEqualTo(RoundingMode.UP);
        assertThat(ConvertableCurrency.USD.getRoundingMode()).isEqualTo(RoundingMode.HALF_EVEN);
    }

}
