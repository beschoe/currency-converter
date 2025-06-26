package com.mercateo.common.currency;

import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Fork(value = 3, warmups = 1)
@Measurement(iterations = 5, time = 4, timeUnit = TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 4, timeUnit = TimeUnit.SECONDS)
public class CurrencyConverterBenchmark {

    private static final Money EUR_RATE = new Money(BigDecimal.ONE, ConvertableCurrency.EUR);
    private static final Money HUF_RATE = new Money(new BigDecimal("400"), ConvertableCurrency.HUF);
    private static final Money USD_RATE = new Money(new BigDecimal("1.09"), ConvertableCurrency.USD);
    private static final Money GBP_RATE = new Money(new BigDecimal("0.84"), ConvertableCurrency.GBP);
    private static final FrozenCurrencyConverter moneyExchange = new FrozenCurrencyConverter(asList(
            new ExchangeRate(EUR_RATE, EUR_RATE), new ExchangeRate(EUR_RATE, HUF_RATE),
            new ExchangeRate(EUR_RATE, GBP_RATE), new ExchangeRate(EUR_RATE, USD_RATE)));
    private static final BigDecimal RATE_VALUE_GBP_TO_USD = new BigDecimal("1.297619058");
    private static final BigDecimal RATE_VALUE_EUR_TO_USD = new BigDecimal("1.09");
    private static final ExchangeRate RATE_USD_TO_EUR = moneyExchange.getExchangeRate(ConvertableCurrency.USD, ConvertableCurrency.EUR);
    private static final ExchangeRate RATE_GBP_TO_USD = moneyExchange.getExchangeRate(ConvertableCurrency.GBP, ConvertableCurrency.USD);

    @Setup(Level.Iteration)
    public void setUp() {/**/}

    @Benchmark
    public Money moneyExchange_highPrecision() {
        final Money price = new Money(new BigDecimal("3.12345"), ConvertableCurrency.GBP);
        final Money convertedPrice = moneyExchange.convertToPrice(price, ConvertableCurrency.USD);
        return convertedPrice;
    }
    @Benchmark
    public Money money_highPrecision() {
        final Money price = new Money(new BigDecimal("3.12345"), ConvertableCurrency.GBP);
        final Money convertedPrice = RATE_GBP_TO_USD.convert(price, DecimalPlacesStrategy.PROPORTIONAL, RoundingMode.HALF_EVEN);
        return convertedPrice;
    }

    @Benchmark
    public BigDecimal bigDecimal_highPrecision() {
        BigDecimal amount = new BigDecimal("3.12345");
        BigDecimal result = amount.multiply(RATE_VALUE_GBP_TO_USD).setScale(5, RoundingMode.HALF_EVEN);
        return result;
    }

    @Benchmark
    public Money moneyExchange_lowPrecision() {
        final Money price = new Money(new BigDecimal("3.12"), ConvertableCurrency.EUR);
        final Money convertedPrice = moneyExchange.convertToPrice(price, ConvertableCurrency.USD);
        return convertedPrice;
    }

    @Benchmark
    public Money money_lowPrecision() {
        final Money price = new Money(new BigDecimal("3.12"), ConvertableCurrency.EUR);
        final Money convertedPrice = RATE_USD_TO_EUR.convert(price, DecimalPlacesStrategy.PROPORTIONAL, RoundingMode.HALF_EVEN);
        return convertedPrice;
    }

    @Benchmark
    public BigDecimal bigDecimal_lowPrecision() {
        BigDecimal amount = new BigDecimal("3.12");
        BigDecimal result = amount.multiply(RATE_VALUE_EUR_TO_USD).setScale(2, RoundingMode.HALF_EVEN);
        return result;
    }
}