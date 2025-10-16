/*
 * Created on 19 Mar 2025
 *
 * author dimitry
 */
package com.mercateo.common.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.mercateo.common.util.annotations.Nullable;

/**
 * Represents an exchange rate between two currencies.
 *
 * An exchange rate consists of a base currency (with value 1.0) and a quote currency,
 * defining how much of the quote currency equals one unit of the base currency.
 * This class also handles caching of combined rates for efficient conversion between currencies.
 */
public class ExchangeRate {
    /**
     * If division needs to be performed, this is the rounding mode.
     */
    private static final RoundingMode DIVISION_ROUNDING_MODE = RoundingMode.HALF_EVEN;

    /**
     * If division needs to be performed, use this as maximum scale.
     */
    private static final int DIVISION_SCALE = 10;

    static ExchangeRate identity(ConvertableCurrency currency) {
        Money money = new Money(BigDecimal.ONE, currency);
        return new ExchangeRate(money, money);
    }

    private final Money baseValue;
    private final Money quoteValue;
    private @Nullable Money rateValue;


    public ExchangeRate(Money baseValue, Money quoteValue) {
        this.baseValue = baseValue;
        this.quoteValue = quoteValue;
    }

    public Money getBaseValue() {
        return baseValue;
    }

    public Money getQuoteValue() {
        return quoteValue;
    }

    public Money getRateValue() {
        if(rateValue != null)
            return rateValue;
        BigDecimal quoteAmount = quoteValue.getAmount();
        BigDecimal baseAmount = baseValue.getAmount();
        return rateValue = new Money(
                quoteAmount.divide(baseAmount, DIVISION_SCALE, DIVISION_ROUNDING_MODE).stripTrailingZeros(),
                quoteValue.getCurrency());
    }

    /**
     * Converts a monetary amount using this exchange rate with the specified decimal places strategy
     * and rounding mode.
     */
    Money convert(Money from, DecimalPlacesStrategy decimalPlacesStrategy, RoundingMode roundingMode) {
        Money exchangeRate = getRateValue();
        ConvertableCurrency rateCurrency = exchangeRate.getCurrency();
        final int newScale = decimalPlacesStrategy.getRequiredScale(from, rateCurrency);
        BigDecimal convertedAmount = from.getAmount().multiply(exchangeRate.getAmount());
        return new Money(convertedAmount.setScale(newScale, roundingMode), rateCurrency);
    }

    ConvertableCurrency getQuoteCurrency() {
        return quoteValue.getCurrency();
    }

    ConvertableCurrency getBaseCurrency() {
        return baseValue.getCurrency();
    }

    ExchangeRate invert() {
        return new ExchangeRate(quoteValue, baseValue);
    }

    ExchangeRate withBase(ExchangeRate denominator) {
        if(! baseValue.equals(denominator.baseValue))
            throw new IllegalArgumentException("Different base values");
        return new ExchangeRate(denominator.quoteValue, quoteValue);
    }

    /**
     * Composes this exchange rate with another to create a synthetic rate.
     * This rate's quote currency must match the other rate's base currency.
     * The resulting rate converts from this rate's base to the other rate's quote.
     * 
     * Example: EUR->USD composed with USD->GBP yields EUR->GBP
     */
    ExchangeRate compose(ExchangeRate next) {
        if (!this.getQuoteCurrency().equals(next.getBaseCurrency())) {
            throw new IllegalArgumentException(
                "Cannot compose rates: quote currency " + this.getQuoteCurrency() + 
                " does not match base currency " + next.getBaseCurrency());
        }
        // Multiply the rate values to get the composed rate
        Money thisRate = this.getRateValue();
        Money nextRate = next.getRateValue();
        BigDecimal composedRate = thisRate.getAmount().multiply(nextRate.getAmount());
        
        // Create normalized base and quote (base amount = 1.0)
        Money normalizedBase = new Money(BigDecimal.ONE, this.getBaseCurrency());
        Money composedQuote = new Money(composedRate, next.getQuoteCurrency());
        return new ExchangeRate(normalizedBase, composedQuote);
    }

    @Override
    public String toString() {
        return "ExchangeRate [1 " + getBaseCurrency() + " -> " + getRateValue().getAmount() + " " + getQuoteCurrency() + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseValue, quoteValue);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExchangeRate other = (ExchangeRate) obj;
        return Objects.equals(baseValue, other.baseValue) &&
               Objects.equals(quoteValue, other.quoteValue);
    }

}