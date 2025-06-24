/*
 * Created on 24 Jun 2025
 *
 * author dimitry
 */
package com.mercateo.common.currency;

import java.math.RoundingMode;

public class UpdateableCurrencyConverter implements CurrencyConverter {
    private volatile CurrencyConverter updatedConverter;

    public UpdateableCurrencyConverter(CurrencyConverter initialConverter) {
        super();
        this.updatedConverter = initialConverter;
    }

    public void set(CurrencyConverter newConverter) {
        updatedConverter = newConverter;
    }

    @Override
    public Money convertToPrice(Money fromAmount, ConvertableCurrency toCurrency)
            throws IllegalArgumentException {
        return updatedConverter.convertToPrice(fromAmount, toCurrency);
    }

    @Override
    public Money convertProportionally(Money fromAmount, ConvertableCurrency toCurrency)
            throws IllegalArgumentException {
        return updatedConverter.convertProportionally(fromAmount, toCurrency);
    }

    @Override
    public Money convert(Money fromAmount, ConvertableCurrency toCurrency,
            DecimalPlacesStrategy decimalPlacesStrategy, RoundingMode roundingMode)
            throws IllegalArgumentException {
        return updatedConverter.convert(fromAmount, toCurrency, decimalPlacesStrategy,
                roundingMode);
    }

    @Override
    public ExchangeRate getExchangeRate(ConvertableCurrency fromCurrency,
            ConvertableCurrency toCurrency) throws IllegalArgumentException {
        return updatedConverter.getExchangeRate(fromCurrency, toCurrency);
    }


}
