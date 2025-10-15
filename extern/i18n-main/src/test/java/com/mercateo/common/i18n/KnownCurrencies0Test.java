package com.mercateo.common.i18n;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class KnownCurrencies0Test {

    @Test
    public void determineDefaultScaleForCurrency_returns_0_as_default_scale_for_HU_currency() {
        assertEquals(0, KnownCurrencies.determineDefaultScaleForCurrency(
                KnownCurrencies.HUF.currency));
    }

    @Test
    public void determineDefaultScaleForCurrency_returns_2_as_default_scale_for_EUR_currency() {
        assertEquals(2, KnownCurrencies.determineDefaultScaleForCurrency(
                KnownCurrencies.EUR.currency));
    }

    @Test
    public void determineDefaultScaleForCurrency_returns_correct_values_for_knownCurrencies() {
        // there are places like formatter strings, or some code in
        // LoaderPriceCalculator.calculateAndNormalizePrices which are only
        // handled correctly or the 0 and 2 case.
        // this test is a reminder to check those.
        for (KnownCurrencies e : KnownCurrencies.values()) {
            int scale = KnownCurrencies.determineDefaultScaleForCurrency(e.currency);
            assertTrue(scale == 2 || scale == 0);
        }
    }

    @Test
    public void determineHighPrecisionScaleForCurrency_returns_3_as_scale_for_HU_currency() {
        assertEquals(3, KnownCurrencies.determineHighPrecisionScaleForCurrency(KnownCurrencies.HUF.currency));
    }

    @Test
    public void determineHighPrecisionScaleForCurrency_returns_5_as_scale_for_EUR_currency() {
        assertEquals(5, KnownCurrencies.determineHighPrecisionScaleForCurrency(KnownCurrencies.EUR.currency));
    }
}
