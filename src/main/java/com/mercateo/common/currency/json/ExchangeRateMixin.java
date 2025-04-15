package com.mercateo.common.currency.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mercateo.common.currency.Money;

/**
 * Jackson mixin class for ExchangeRate serialization/deserialization.
 * This mixin provides JSON annotations without modifying the original ExchangeRate class.
 */
abstract class ExchangeRateMixin {

    @SuppressWarnings("unused")
    @JsonCreator
    public ExchangeRateMixin(
        @JsonProperty("baseValue") Money baseValue,
        @JsonProperty("quoteValue") Money quoteValue
    ) {/**/}

    @JsonProperty("baseValue")
    abstract Money getBaseValue();

    @JsonProperty("quoteValue")
    abstract Money getQuoteValue();
}