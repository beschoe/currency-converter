package com.mercateo.common.currency.json;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mercateo.common.currency.ConvertableCurrency;


/**
 * Jackson mixin class for Money serialization/deserialization.
 * This mixin provides JSON annotations without modifying the original Money class.
 */
abstract class MoneyMixin {
    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    private ConvertableCurrency currency;

    @SuppressWarnings({ "unused", "null" })
    @JsonCreator
    public MoneyMixin(
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("currency") ConvertableCurrency currency
    ) {/**/}
}