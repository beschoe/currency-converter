package com.mercateo.common.i18n;

import java.util.Currency;

public enum KnownCurrencies {
    EUR,
    USD,
    GBP,
    PLN,
    CZK,
    CHF,
    DKK,
    HRK,
    SEK,
    BGN,
    HUF,
    LVL,
    LTL,
    RON,
    TRY,
    DEM,
    CNY,
    INR,
    BRL,
    MXN;

    public static KnownCurrencies parseFromCurrency(Currency currency) {
        return valueOf(currency.getCurrencyCode());
    }
}
