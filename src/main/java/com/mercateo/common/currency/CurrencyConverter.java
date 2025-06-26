/*
 * Created on 24 Jun 2025
 *
 * author dimitry
 */
package com.mercateo.common.currency;

import java.math.RoundingMode;

/**
 * Handles conversion of monetary amounts between different currencies.
 *
 * This interface provides methods
 * to convert between any supported currencies.
 */
public interface CurrencyConverter {

    /**
     * Converts a monetary amount using invoice-specific decimal places.
     * Specifically designed for invoice calculations where special decimal place rules apply.
     *
     * @throws IllegalArgumentException if an exchange rate for either currency is not found
     */
    Money convertToPrice(Money fromAmount, ConvertableCurrency toCurrency)
            throws IllegalArgumentException;

    /**
     * Converts a monetary amount using calculation-specific decimal places.
     * Designed for general calculations where standard decimal place rules apply.
     *
     * @throws IllegalArgumentException if an exchange rate for either currency is not found
     */
    Money convertProportionally(Money fromAmount, ConvertableCurrency toCurrency)
            throws IllegalArgumentException;

    /**
     * Converts a monetary amount using the provided decimal places strategy and rounding mode.
     *
     * @throws IllegalArgumentException if an exchange rate for either currency is not found
     */
    Money convert(Money fromAmount, ConvertableCurrency toCurrency,
            DecimalPlacesStrategy decimalPlacesStrategy, RoundingMode roundingMode)
            throws IllegalArgumentException;

    /**
     * Gets the exchange rate between two currencies.
     *
     * @throws IllegalArgumentException if an exchange rate for either currency is not found
     */
    ExchangeRate getExchangeRate(ConvertableCurrency fromCurrency, ConvertableCurrency toCurrency)
            throws IllegalArgumentException;

}