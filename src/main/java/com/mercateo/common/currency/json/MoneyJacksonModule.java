package com.mercateo.common.currency.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mercateo.common.currency.ExchangeRate;
import com.mercateo.common.currency.Money;

/**
 * Jackson module for serializing and deserializing Money and ExchangeRate classes
 * without adding JSON annotations to the domain classes.
 */
public class MoneyJacksonModule extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public MoneyJacksonModule() {
        super("MoneyJacksonModule", new Version(1, 0, 0, null, "com.mercateo.common.i18n", "money-json"));

        // Register mixins
        setMixInAnnotation(Money.class, MoneyMixin.class);
        setMixInAnnotation(ExchangeRate.class, ExchangeRateMixin.class);
    }
}