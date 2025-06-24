/*
 * Created on 19 Mar 2025
 *
 * author dimitry
 */
package com.mercateo.common.currency;

import static com.mercateo.common.currency.ConvertableCurrency.BRL;
import static com.mercateo.common.currency.ConvertableCurrency.DEM;
import static com.mercateo.common.currency.ConvertableCurrency.EUR;
import static com.mercateo.common.currency.ConvertableCurrency.GBP;
import static com.mercateo.common.currency.ConvertableCurrency.HUF;
import static com.mercateo.common.currency.ConvertableCurrency.USD;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.Test;

public class FixedRatesCurrencyConverterTest {
    private static final Money EUR_RATE = new Money(BigDecimal.ONE, EUR);
    private static final Money HUF_RATE = new Money(new BigDecimal("400"), HUF);
    private static final Money USD_RATE = new Money(new BigDecimal("1.09"), USD);
    private static final Money GBP_RATE = new Money(new BigDecimal("0.84"), GBP);
    private static final Money DEM_RATE = new Money(new BigDecimal("1.95583"), DEM);

    private static final List<ExchangeRate> rates = asList(
            new ExchangeRate(EUR_RATE, HUF_RATE),
            new ExchangeRate(EUR_RATE, GBP_RATE),
            new ExchangeRate(EUR_RATE, USD_RATE),
            new ExchangeRate(EUR_RATE, DEM_RATE));
    private static final FixedRatesCurrencyConverter uut = new FixedRatesCurrencyConverter(rates);

    @Test
    public void convertPriceInEUR_toPriceInHUF_forInvoice() throws Exception {
        final Money price = new Money(new BigDecimal("1.010101"),EUR);
        final Money convertedPrice = uut.convertToPrice(price,HUF);
        assertThat(convertedPrice).isEqualTo(new Money(new BigDecimal("405"),HUF));
    }

    @Test
    public void convertPriceInEUR_toPriceInHUF_forCalculations() throws Exception {
        final Money price = new Money(new BigDecimal("1.010101"),EUR);
        final Money convertedPrice = uut.convertProportionally(price,HUF);
        assertThat(convertedPrice).isEqualTo(new Money(new BigDecimal("404.0404"),HUF));
    }

    @Test
    public void convertPriceInHUF_toPriceInEUR() throws Exception {
        final Money price = new Money(new BigDecimal("1000"),HUF);
        final Money convertedPrice = uut.convertToPrice(price,EUR);
        assertThat(convertedPrice).isEqualTo(new Money(new BigDecimal("2.50"),EUR));
    }

    @Test
    public void convertHighPrecisionPriceInEUR_toPriceInHUF() throws Exception {
        final Money price = new Money(new BigDecimal("1.0000"),EUR);
        final Money convertedPrice = uut.convertProportionally(price,HUF);
        assertThat(convertedPrice).isEqualTo(new Money(new BigDecimal("400.00"),HUF));
    }

    @Test
    public void convertPriceInEUR_toPriceInUSD() throws Exception {
        final Money price = new Money(new BigDecimal("1.00"), EUR);
        final Money convertedPrice = uut.convertToPrice(price, USD);
        assertThat(convertedPrice).isEqualTo(new Money(new BigDecimal("1.09"), USD));
    }

    @Test
    public void convertPriceInUSD_toPriceInEUR() throws Exception {
        final Money price = new Money(new BigDecimal("1.00"),USD);
        final Money convertedPrice = uut.convertToPrice(price, EUR);
        assertThat(convertedPrice).isEqualTo(new Money(new BigDecimal("0.92"), EUR));
    }

    @Test
    public void convertPriceInUSD_toPriceInUSD_withDecimalPlacesForInfoice() throws Exception {
        final Money price = new Money(new BigDecimal("1.0101"),USD);
        final Money convertedPrice = uut.convertToPrice(price, USD);
        assertThat(convertedPrice).isEqualTo(new Money(new BigDecimal("1.01"), USD));
    }


    @Test
    public void convertPriceInUSD_toPriceInUSD_withDecimalPlacesForCalculations() throws Exception {
        final Money price = new Money(new BigDecimal("1.0101"),USD);
        final Money convertedPrice = uut.convertProportionally(price, USD);
        assertThat(convertedPrice).isEqualTo(new Money(new BigDecimal("1.0101"), USD));
    }

    @Test
    public void convertHighPrecisionPriceInGBP_toPriceInUSD_forInvoice() throws Exception {
        final Money price = new Money(new BigDecimal("3.12345"), GBP);
        final Money convertedPrice = uut.convertToPrice(price, USD);
        assertThat(convertedPrice).isEqualTo(new Money(new BigDecimal("4.05"), USD));
    }

    @Test
    public void convertHighPrecisionPriceInGBP_toPriceInUSD() throws Exception {
        final Money price = new Money(new BigDecimal("3.12345"), GBP);
        final Money convertedPrice = uut.convertProportionally(price, USD);
        assertThat(convertedPrice).isEqualTo(new Money(new BigDecimal("4.05305"), USD));
    }

    @Test
    public void convertPriceInGBP_toPriceInUSD() throws Exception {
        final Money price = new Money(new BigDecimal("1.00"), GBP);
        final Money convertedPrice = uut.convertToPrice(price, USD);
        assertThat(convertedPrice).isEqualTo(new Money(new BigDecimal("1.30"), USD));
    }

    @Test
    public void convertPriceInUSD_toPriceInGBP() throws Exception {
        final Money price = new Money(new BigDecimal("1.00"), USD);
        final Money convertedPrice = uut.convertToPrice(price, GBP);
        assertThat(convertedPrice).isEqualTo(new Money(new BigDecimal("0.77"), GBP));
    }

    @Test
    public void convertPriceInEUR_toPriceInDEM_roundingUp() throws Exception {
        final Money price = new Money(new BigDecimal("1"), EUR);
        final Money convertedPrice = uut.convert(price, DEM,
                DecimalPlacesStrategy.PROPORTIONAL, RoundingMode.UP);
        assertThat(convertedPrice).isEqualTo(new Money(new BigDecimal("2"),DEM));
    }

    @Test
    public void convertPriceInEUR_toPriceInDEM_roundingDown() throws Exception {
        final Money price = new Money(new BigDecimal("1"), EUR);
        final Money convertedPrice = uut.convert(price, DEM,
                DecimalPlacesStrategy.PROPORTIONAL, RoundingMode.DOWN);
        assertThat(convertedPrice).isEqualTo(new Money(new BigDecimal("1"),DEM));
    }

    @Test
    public void calculatesExchangeRates() throws Exception {
        assertThat(uut.getExchangeRate(EUR, EUR).getRateValue().getAmount()).isEqualTo(BigDecimal.ONE);
        assertThat(uut.getExchangeRate(BRL, BRL).getRateValue().getAmount()).isEqualTo(BigDecimal.ONE);

        assertThat(uut.getExchangeRate(EUR, HUF).getRateValue().getAmount()).isEqualByComparingTo(HUF_RATE.getAmount());
        assertThat(uut.getExchangeRate(HUF, EUR).getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("0.0025"));

        assertThat(uut.getExchangeRate(EUR, GBP).getRateValue().getAmount()).isEqualTo(GBP_RATE.getAmount());
        assertThat(uut.getExchangeRate(GBP, EUR).getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("1.1904761905"));

        assertThat(uut.getExchangeRate(EUR, USD).getRateValue().getAmount()).isEqualTo(USD_RATE.getAmount());
        assertThat(uut.getExchangeRate(USD, EUR).getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("0.9174311927"));

        assertThat(uut.getExchangeRate(GBP, USD).getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("1.2976190476"));
        assertThat(uut.getExchangeRate(USD, GBP).getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("0.7706422018"));
    }

    @Test
    public void ignoresRepeatedExchangeRatesInConstructor() throws Exception {
        FixedRatesCurrencyConverter uut = new FixedRatesCurrencyConverter(asList(
                new ExchangeRate(EUR_RATE, EUR_RATE),
                new ExchangeRate(EUR_RATE, HUF_RATE),
                new ExchangeRate(EUR_RATE, HUF_RATE)));
        assertThat(uut.getExchangeRate(EUR, HUF).getRateValue().getAmount()).isEqualByComparingTo(HUF_RATE.getAmount());
    }

    @Test
    public void failsOnInconsistentExchangeRatesInConstructor() throws Exception {
        assertThatThrownBy(() ->new FixedRatesCurrencyConverter(asList(
                new ExchangeRate(EUR_RATE, new Money(new BigDecimal("400"), HUF)),
                new ExchangeRate(EUR_RATE, new Money(new BigDecimal("500"), HUF)))))
        .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void failsOnUnknownExchangeRates() throws Exception {
        assertThatThrownBy(() ->uut.convertToPrice(EUR_RATE, BRL))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void failsExchangeRatesFromDifferentClusters() throws Exception {
        Money usdBase = new Money(BigDecimal.ONE, USD);
        FixedRatesCurrencyConverter uut = new FixedRatesCurrencyConverter(asList(
                new ExchangeRate(EUR_RATE, EUR_RATE),
                new ExchangeRate(EUR_RATE, HUF_RATE),
                new ExchangeRate(usdBase, usdBase),
                new ExchangeRate(usdBase, GBP_RATE)));
        assertThatThrownBy(() ->uut.convertToPrice(EUR_RATE, GBP))
        .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() ->uut.convertToPrice(GBP_RATE, EUR))
        .isInstanceOf(IllegalArgumentException.class);
    }
}
