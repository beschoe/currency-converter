package com.mercateo.common.i18n;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;


/**
 * Created by till on 20.11.15.
 */
public class CurrencyCalculator0Test {

    @Test
    public void testHungarianRounding() throws Exception {
        // NOTE: formally, HUF has 2 fractionDigits "filler", but they are no
        // longer used in practice
        assertEquals(2, KnownCurrencies.HUF.currency.getDefaultFractionDigits());

        assertEquals(CurrencyCalculator.applyCurrencyRate(new BigDecimal("22.00"), BigDecimal.ONE,
                KnownCurrencies.HUF.currency), new BigDecimal("22"));
        assertEquals(CurrencyCalculator.applyCurrencyRate(new BigDecimal("22.01"), BigDecimal.ONE,
                KnownCurrencies.HUF.currency), new BigDecimal("23"));

        assertEquals(CurrencyCalculator.applyCurrencyRate(new BigDecimal("22.00"), BigDecimal.ONE,
                KnownCurrencies.EUR.currency), new BigDecimal("22.00"));
        assertEquals(CurrencyCalculator.applyCurrencyRate(new BigDecimal("22.01"), BigDecimal.ONE,
                KnownCurrencies.EUR.currency), new BigDecimal("22.01"));
    }

    @Test
    public void testApplyCurrencyRateExact() {
        assertEquals(CurrencyCalculator.applyCurrencyRateExact(new BigDecimal("22.01"),
                BigDecimal.ONE), new BigDecimal("22.01"));
    }
}