package com.mercateo.common.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class DecimalFormatterFactoryTest {
    private DecimalFormatterFactory uut;

    @UseDataProvider("priceFormatterSettings")
    @Test
    public void priceFormatter(KnownCurrencies currency, boolean roundHUF, boolean expectedHUFFormat) {
        // given
        uut = new DecimalFormatterFactory(
                "thePriceFormat #",
                "thePriceFormatHUF #",
                "theQuantityFormat #", Locale.GERMANY, currency, roundHUF);

        // when
        String result = uut.getPriceFormatter().toPattern();

        // then
        assertThat(result).isEqualTo(expectedHUFFormat ? "thePriceFormatHUF #" : "thePriceFormat #");
    }

    @DataProvider
    public static Object[][] priceFormatterSettings() {
        return new Object[][] {
                new Object[] { KnownCurrencies.EUR, true, false },
                new Object[] { KnownCurrencies.HUF, true, true },
                new Object[] { KnownCurrencies.EUR, false, false },
                new Object[] { KnownCurrencies.HUF, false, false }
        };
    }
}
