/*
 * Created on 24 Jun 2025
 *
 * author dimitry
 */
package com.mercateo.common.currency;

import java.math.RoundingMode;

/**
 * A thread-safe wrapper around a {@link CurrencyConverter} that allows updating the underlying 
 * converter implementation at runtime. This class delegates all conversion operations to the 
 * currently set converter instance.
 * 
 * <p>This implementation uses the decorator pattern to provide a mutable converter that can 
 * switch between different conversion strategies without changing the client code that uses it.
 * The converter reference is declared as volatile to ensure thread-safe updates.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * CurrencyConverter initialConverter = new FixedRatesCurrencyConverter(rates);
 * UpdateableCurrencyConverter converter = new UpdateableCurrencyConverter(initialConverter);
 * 
 * // Use the converter
 * Money result = converter.convertToPrice(money, targetCurrency);
 * 
 * // Later, update to a different converter
 * CurrencyConverter newConverter = new FixedRatesCurrencyConverter(newRates);
 * converter.set(newConverter);
 * </pre>
 */
public class UpdateableCurrencyConverter implements CurrencyConverter {
    
    /**
     * The current converter instance. Declared as volatile to ensure thread-safe updates.
     */
    private volatile CurrencyConverter updatedConverter;

    /**
     * Creates a new UpdateableCurrencyConverter with the specified initial converter.
     * 
     * @param initialConverter the initial converter to delegate to; must not be null
     * @throws NullPointerException if initialConverter is null
     */
    public UpdateableCurrencyConverter(CurrencyConverter initialConverter) {
        super();
        this.updatedConverter = initialConverter;
    }

    /**
     * Updates the underlying converter to the specified new converter.
     * This operation is thread-safe and will take effect immediately for all subsequent conversions.
     * 
     * @param newConverter the new converter to delegate to; must not be null
     * @throws NullPointerException if newConverter is null
     */
    public void set(CurrencyConverter newConverter) {
        updatedConverter = newConverter;
    }

    /**
     * {@inheritDoc}
     * 
     * Delegates to the currently set converter.
     */
    @Override
    public Money convertToPrice(Money fromAmount, ConvertableCurrency toCurrency)
            throws IllegalArgumentException {
        return updatedConverter.convertToPrice(fromAmount, toCurrency);
    }

    /**
     * {@inheritDoc}
     * 
     * Delegates to the currently set converter.
     */
    @Override
    public Money convertProportionally(Money fromAmount, ConvertableCurrency toCurrency)
            throws IllegalArgumentException {
        return updatedConverter.convertProportionally(fromAmount, toCurrency);
    }

    /**
     * {@inheritDoc}
     * 
     * Delegates to the currently set converter.
     */
    @Override
    public Money convert(Money fromAmount, ConvertableCurrency toCurrency,
            DecimalPlacesStrategy decimalPlacesStrategy, RoundingMode roundingMode)
            throws IllegalArgumentException {
        return updatedConverter.convert(fromAmount, toCurrency, decimalPlacesStrategy,
                roundingMode);
    }

    /**
     * {@inheritDoc}
     * 
     * Delegates to the currently set converter.
     */
    @Override
    public ExchangeRate getExchangeRate(ConvertableCurrency fromCurrency,
            ConvertableCurrency toCurrency) throws IllegalArgumentException {
        return updatedConverter.getExchangeRate(fromCurrency, toCurrency);
    }

}
