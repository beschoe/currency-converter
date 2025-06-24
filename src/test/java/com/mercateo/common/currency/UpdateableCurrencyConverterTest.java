/*
 * Created on 24 Jun 2025
 *
 * author dimitry
 */
package com.mercateo.common.currency;

import static com.mercateo.common.currency.ConvertableCurrency.EUR;
import static com.mercateo.common.currency.ConvertableCurrency.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

public class UpdateableCurrencyConverterTest {

    private final CurrencyConverter mockConverter = mock(CurrencyConverter.class);
    private final UpdateableCurrencyConverter uut = new UpdateableCurrencyConverter(mockConverter);

    private final Money eurAmount = new Money(new BigDecimal("100.00"), EUR);
    private final Money usdAmount = new Money(new BigDecimal("109.00"), USD);
    private final ExchangeRate exchangeRate = new ExchangeRate(
            new Money(BigDecimal.ONE, EUR), 
            new Money(new BigDecimal("1.09"), USD));

    @Test
    public void convertToPrice_delegatesToUnderlyingConverter() throws Exception {
        when(mockConverter.convertToPrice(eurAmount, USD)).thenReturn(usdAmount);

        Money result = uut.convertToPrice(eurAmount, USD);

        assertThat(result).isEqualTo(usdAmount);
        verify(mockConverter).convertToPrice(eurAmount, USD);
        verifyNoMoreInteractions(mockConverter);
    }

    @Test
    public void convertProportionally_delegatesToUnderlyingConverter() throws Exception {
        Money expectedAmount = new Money(new BigDecimal("109.0000"), USD);
        when(mockConverter.convertProportionally(eurAmount, USD)).thenReturn(expectedAmount);

        Money result = uut.convertProportionally(eurAmount, USD);

        assertThat(result).isEqualTo(expectedAmount);
        verify(mockConverter).convertProportionally(eurAmount, USD);
        verifyNoMoreInteractions(mockConverter);
    }

    @Test
    public void convert_delegatesToUnderlyingConverter() throws Exception {
        DecimalPlacesStrategy strategy = DecimalPlacesStrategy.TO_PRICE;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        when(mockConverter.convert(eurAmount, USD, strategy, roundingMode)).thenReturn(usdAmount);

        Money result = uut.convert(eurAmount, USD, strategy, roundingMode);

        assertThat(result).isEqualTo(usdAmount);
        verify(mockConverter).convert(eurAmount, USD, strategy, roundingMode);
        verifyNoMoreInteractions(mockConverter);
    }

    @Test
    public void getExchangeRate_delegatesToUnderlyingConverter() throws Exception {
        when(mockConverter.getExchangeRate(EUR, USD)).thenReturn(exchangeRate);

        ExchangeRate result = uut.getExchangeRate(EUR, USD);

        assertThat(result).isEqualTo(exchangeRate);
        verify(mockConverter).getExchangeRate(EUR, USD);
        verifyNoMoreInteractions(mockConverter);
    }

    @Test
    public void set_updatesUnderlyingConverter() throws Exception {
        CurrencyConverter newMockConverter = mock(CurrencyConverter.class);
        Money differentResult = new Money(new BigDecimal("110.00"), USD);
        when(newMockConverter.convertToPrice(eurAmount, USD)).thenReturn(differentResult);

        uut.set(newMockConverter);
        Money result = uut.convertToPrice(eurAmount, USD);

        assertThat(result).isEqualTo(differentResult);
        verify(newMockConverter).convertToPrice(eurAmount, USD);
        verifyNoMoreInteractions(mockConverter);
        verifyNoMoreInteractions(newMockConverter);
    }

    @Test
    public void set_switchesBetweenMultipleConverters() throws Exception {
        CurrencyConverter firstConverter = mock(CurrencyConverter.class);
        CurrencyConverter secondConverter = mock(CurrencyConverter.class);
        
        Money firstResult = new Money(new BigDecimal("108.00"), USD);
        Money secondResult = new Money(new BigDecimal("110.00"), USD);
        
        when(firstConverter.convertToPrice(eurAmount, USD)).thenReturn(firstResult);
        when(secondConverter.convertToPrice(eurAmount, USD)).thenReturn(secondResult);

        uut.set(firstConverter);
        Money result1 = uut.convertToPrice(eurAmount, USD);
        assertThat(result1).isEqualTo(firstResult);

        uut.set(secondConverter);
        Money result2 = uut.convertToPrice(eurAmount, USD);
        assertThat(result2).isEqualTo(secondResult);

        uut.set(firstConverter);
        Money result3 = uut.convertToPrice(eurAmount, USD);
        assertThat(result3).isEqualTo(firstResult);

        verify(firstConverter, times(2)).convertToPrice(eurAmount, USD);
        verify(secondConverter).convertToPrice(eurAmount, USD);
    }

} 