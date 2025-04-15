/*
 * Created on 19 Mar 2025
 *
 * author dimitry
 */
package com.mercateo.common.currency;

import java.math.RoundingMode;
import java.util.Collection;
import java.util.EnumMap;
import java.util.function.Function;

import com.mercateo.common.util.annotations.NonNull;

/**
 * Handles conversion of monetary amounts between different currencies.
 *
 * This class manages a collection of exchange rates and provides methods
 * to convert between any supported currencies. It requires all exchange
 * rates to be specified relative to a common base currency.
 */
public class CurrencyConverter {
    private final EnumMap<ConvertableCurrency, ExchangeRate> directRates;
    private final EnumMap<ConvertableCurrency, EnumMap<ConvertableCurrency, ExchangeRate>> rates;

    /**
     * Creates a new currency converter with the specified exchange rates.
     *
     * @throws IllegalStateException if duplicate exchange rates are provided
     */
    public CurrencyConverter(Collection<ExchangeRate> rateCollection) throws IllegalStateException {
        super();
        this.directRates = new EnumMap<>(ConvertableCurrency.class);
        rateCollection.forEach(rate -> directRates.put(rate.getQuoteCurrency(),  rate));
        this.rates = new EnumMap<>(ConvertableCurrency.class);
        rateCollection.forEach(this::addExchangeRate);
        rateCollection.stream().map(ExchangeRate::invert)
        .forEach(rate -> computeExchangeRateIfAbsent(rate.getBaseCurrency(), rate.getQuoteCurrency(), x -> rate));
    }


    @SuppressWarnings("null")
    private void addExchangeRate(@NonNull ExchangeRate rate) {
        ExchangeRate knownRate = rates.computeIfAbsent(rate.getBaseCurrency(), x -> new EnumMap<>(ConvertableCurrency.class))
                .put(rate.getQuoteCurrency(),  rate);
        if(knownRate != null && ! knownRate.equals(rate))
            throw new IllegalStateException("conflicting rates " + rate + " and " + knownRate);
    }


    /**
     * Converts a monetary amount to the specified currency using the currency's default rounding mode.
     *
     * @throws IllegalArgumentException if an exchange rate for either currency is not found
     */
    public Money convert(Money fromAmount, ConvertableCurrency toCurrency) throws IllegalArgumentException {
        return convert(fromAmount, toCurrency, toCurrency.getRoundingMode());
    }

    /**
     * Converts a monetary amount to the specified currency using the provided rounding mode.
     *
     * @throws IllegalArgumentException if an exchange rate for either currency is not found
     */
    public Money convert(Money fromAmount, ConvertableCurrency toCurrency, RoundingMode roundingMode) throws IllegalArgumentException {
        if(fromAmount.getCurrency().equals(toCurrency))
            return fromAmount;
        ExchangeRate exchangeRate = getExchangeRate(fromAmount.getCurrency(), toCurrency);
        return exchangeRate.convert(fromAmount, roundingMode);
    }

    /**
     * Gets the exchange rate between two currencies.
     *
     * @throws IllegalArgumentException if an exchange rate for either currency is not found
     */
    public ExchangeRate getExchangeRate(ConvertableCurrency fromCurrency, ConvertableCurrency toCurrency) throws IllegalArgumentException {
        return computeExchangeRateIfAbsent(fromCurrency, toCurrency,
                fromCurrency == toCurrency ? ExchangeRate::identity : x -> calculateDerivedRate(fromCurrency, toCurrency));
    }

    @SuppressWarnings("null")
    private ExchangeRate calculateDerivedRate(ConvertableCurrency fromCurrency, ConvertableCurrency toCurrency) {
        ExchangeRate knownRate = directRates.get(toCurrency);
        if(knownRate == null)
            throw new IllegalArgumentException("Unknown Currency " + toCurrency);
        ExchangeRate newBaseRate = directRates.get(fromCurrency);
        if(newBaseRate == null)
            throw new IllegalArgumentException("Unknown Currency " + fromCurrency);
        return knownRate.withBase(newBaseRate);
    }


    private ExchangeRate computeExchangeRateIfAbsent(ConvertableCurrency fromCurrency,
            ConvertableCurrency toCurrency, Function<ConvertableCurrency, ExchangeRate> rateCalculator) {
        return rates.computeIfAbsent(fromCurrency, x -> new EnumMap<>(ConvertableCurrency.class))
        .computeIfAbsent(toCurrency, rateCalculator);
    }

    @Override
    public String toString() {
        return "MoneyExchange [rates=" + directRates + "]";
    }
}