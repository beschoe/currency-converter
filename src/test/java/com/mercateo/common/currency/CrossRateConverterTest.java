/*
 * Tests for INC-3: Cross-rate functionality with arbitrary currency pairs
 */
package com.mercateo.common.currency;

import static com.mercateo.common.currency.ConvertableCurrency.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * Tests for the cross-rate functionality that allows:
 * - Unrestricted currency pair ingestion (no single canonical base required)
 * - On-demand synthetic cross-rate calculation
 * - Shortest-hop path selection with 4-hop limit
 * - Direct rate precedence over synthetic rates
 */
public class CrossRateConverterTest {

    @Test
    public void directConversionReturnsExpectedResult() {
        // Happy path: direct conversion with ingested pair
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money usdQuote = new Money(new BigDecimal("1.10"), USD);
        ExchangeRate eurToUsd = new ExchangeRate(eurBase, usdQuote);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(eurToUsd));
        
        Money input = new Money(new BigDecimal("100.00"), EUR);
        Money result = converter.convertToPrice(input, USD);
        
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("110.00"));
        assertThat(result.getCurrency()).isEqualTo(USD);
    }

    @Test
    public void syntheticConversionWithTwoHops() {
        // Synthetic conversion: EUR->GBP via EUR->USD->GBP (2 hops)
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money usdFromEur = new Money(new BigDecimal("1.10"), USD);
        Money usdBase = new Money(BigDecimal.ONE, USD);
        Money gbpFromUsd = new Money(new BigDecimal("0.80"), GBP);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(
            new ExchangeRate(eurBase, usdFromEur),
            new ExchangeRate(usdBase, gbpFromUsd)
        ));
        
        // EUR->GBP should be computed as EUR->USD->GBP = 1.10 * 0.80 = 0.88
        ExchangeRate syntheticRate = converter.getExchangeRate(EUR, GBP);
        assertThat(syntheticRate.getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("0.88"));
        
        Money input = new Money(new BigDecimal("100.00"), EUR);
        Money result = converter.convertToPrice(input, GBP);
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("88.00"));
    }

    @Test
    public void syntheticConversionWithThreeHops() {
        // Synthetic conversion with 3 hops: EUR->USD->GBP->CHF
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money usdFromEur = new Money(new BigDecimal("1.10"), USD);
        Money usdBase = new Money(BigDecimal.ONE, USD);
        Money gbpFromUsd = new Money(new BigDecimal("0.80"), GBP);
        Money gbpBase = new Money(BigDecimal.ONE, GBP);
        Money chfFromGbp = new Money(new BigDecimal("1.25"), CHF);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(
            new ExchangeRate(eurBase, usdFromEur),
            new ExchangeRate(usdBase, gbpFromUsd),
            new ExchangeRate(gbpBase, chfFromGbp)
        ));
        
        // EUR->CHF should be EUR->USD->GBP->CHF = 1.10 * 0.80 * 1.25 = 1.10
        ExchangeRate syntheticRate = converter.getExchangeRate(EUR, CHF);
        assertThat(syntheticRate.getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("1.10"));
    }

    @Test
    public void syntheticConversionWithFourHops() {
        // Synthetic conversion at the 4-hop boundary: EUR->USD->GBP->CHF->DKK
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money usdFromEur = new Money(new BigDecimal("1.10"), USD);
        Money usdBase = new Money(BigDecimal.ONE, USD);
        Money gbpFromUsd = new Money(new BigDecimal("0.80"), GBP);
        Money gbpBase = new Money(BigDecimal.ONE, GBP);
        Money chfFromGbp = new Money(new BigDecimal("1.25"), CHF);
        Money chfBase = new Money(BigDecimal.ONE, CHF);
        Money dkkFromChf = new Money(new BigDecimal("7.00"), DKK);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(
            new ExchangeRate(eurBase, usdFromEur),
            new ExchangeRate(usdBase, gbpFromUsd),
            new ExchangeRate(gbpBase, chfFromGbp),
            new ExchangeRate(chfBase, dkkFromChf)
        ));
        
        // EUR->DKK = 1.10 * 0.80 * 1.25 * 7.00 = 7.70
        ExchangeRate syntheticRate = converter.getExchangeRate(EUR, DKK);
        assertThat(syntheticRate.getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("7.70"));
    }

    @Test
    public void noPathThrowsIllegalArgumentException() {
        // No available path between currencies
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money usdFromEur = new Money(new BigDecimal("1.10"), USD);
        Money gbpBase = new Money(BigDecimal.ONE, GBP);
        Money chfFromGbp = new Money(new BigDecimal("1.25"), CHF);
        
        // Two disconnected clusters: EUR<->USD and GBP<->CHF
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(
            new ExchangeRate(eurBase, usdFromEur),
            new ExchangeRate(gbpBase, chfFromGbp)
        ));
        
        assertThatThrownBy(() -> converter.getExchangeRate(EUR, GBP))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("No exchange rate path found");
    }

    @Test
    public void sameCurrencyConversionPreservesScaling() {
        // Same currency conversion with scaling adjustment
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money usdFromEur = new Money(new BigDecimal("1.10"), USD);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(
            new ExchangeRate(eurBase, usdFromEur)
        ));
        
        Money input = new Money(new BigDecimal("100.123"), EUR);
        Money result = converter.convertToPrice(input, EUR);
        
        // Should scale to currency's default precision for prices (2 for EUR)
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("100.12"));
        assertThat(result.getCurrency()).isEqualTo(EUR);
    }

    @Test
    public void directRateTakesPrecedenceOverSynthetic() {
        // When both direct and synthetic paths exist, direct rate should be used
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money usdFromEur = new Money(new BigDecimal("1.10"), USD);
        Money gbpFromEur = new Money(new BigDecimal("0.85"), GBP); // Direct EUR->GBP
        Money usdBase = new Money(BigDecimal.ONE, USD);
        Money gbpFromUsd = new Money(new BigDecimal("0.80"), GBP); // Synthetic path EUR->USD->GBP
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(
            new ExchangeRate(eurBase, usdFromEur),
            new ExchangeRate(eurBase, gbpFromEur), // Direct rate
            new ExchangeRate(usdBase, gbpFromUsd)
        ));
        
        // Should use direct rate (0.85) not synthetic (1.10 * 0.80 = 0.88)
        ExchangeRate rate = converter.getExchangeRate(EUR, GBP);
        assertThat(rate.getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("0.85"));
    }

    @Test
    public void shortestPathSelectedWhenMultiplePaths() {
        // Multiple paths exist, should select shortest (deterministic)
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money usdFromEur = new Money(new BigDecimal("1.10"), USD);
        Money gbpFromEur = new Money(new BigDecimal("0.85"), GBP);
        Money chfFromEur = new Money(new BigDecimal("0.95"), CHF);
        Money usdBase = new Money(BigDecimal.ONE, USD);
        Money chfFromUsd = new Money(new BigDecimal("0.90"), CHF);
        
        // Two paths from EUR to CHF:
        // 1. Direct: EUR->CHF (1 hop)
        // 2. Via USD: EUR->USD->CHF (2 hops)
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(
            new ExchangeRate(eurBase, usdFromEur),
            new ExchangeRate(eurBase, gbpFromEur),
            new ExchangeRate(eurBase, chfFromEur), // Shortest path
            new ExchangeRate(usdBase, chfFromUsd)
        ));
        
        // Should use direct 1-hop path
        ExchangeRate rate = converter.getExchangeRate(EUR, CHF);
        assertThat(rate.getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("0.95"));
    }

    @Test
    public void deterministicPathSelectionAcrossRuns() {
        // Path selection must be deterministic given same inputs
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money usdFromEur = new Money(new BigDecimal("1.10"), USD);
        Money usdBase = new Money(BigDecimal.ONE, USD);
        Money gbpFromUsd = new Money(new BigDecimal("0.80"), GBP);
        
        FrozenCurrencyConverter converter1 = new FrozenCurrencyConverter(asList(
            new ExchangeRate(eurBase, usdFromEur),
            new ExchangeRate(usdBase, gbpFromUsd)
        ));
        
        FrozenCurrencyConverter converter2 = new FrozenCurrencyConverter(asList(
            new ExchangeRate(eurBase, usdFromEur),
            new ExchangeRate(usdBase, gbpFromUsd)
        ));
        
        BigDecimal rate1 = converter1.getExchangeRate(EUR, GBP).getRateValue().getAmount();
        BigDecimal rate2 = converter2.getExchangeRate(EUR, GBP).getRateValue().getAmount();
        
        assertThat(rate1).isEqualByComparingTo(rate2);
    }

    @Test
    public void arbitraryCurrencyPairsWithNoCanonicalBase() {
        // Unrestricted pair ingestion without requiring single canonical base
        Money usdBase = new Money(BigDecimal.ONE, USD);
        Money eurFromUsd = new Money(new BigDecimal("0.90"), EUR);
        Money gbpBase = new Money(BigDecimal.ONE, GBP);
        Money chfFromGbp = new Money(new BigDecimal("1.20"), CHF);
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money gbpFromEur = new Money(new BigDecimal("0.85"), GBP);
        
        // Three different bases: USD, GBP, EUR
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(
            new ExchangeRate(usdBase, eurFromUsd),
            new ExchangeRate(gbpBase, chfFromGbp),
            new ExchangeRate(eurBase, gbpFromEur)
        ));
        
        // Should be able to convert USD->CHF via USD->EUR->GBP->CHF
        ExchangeRate rate = converter.getExchangeRate(USD, CHF);
        // 0.90 * 0.85 * 1.20 = 0.918
        assertThat(rate.getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("0.918"));
    }

    @Test
    public void beyondFourHopsReturnsNoPath() {
        // Paths longer than 4 hops should not be found
        // Create a chain: EUR->A->B->C->D->USD (5 hops)
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money plnFromEur = new Money(new BigDecimal("4.5"), PLN);
        Money plnBase = new Money(BigDecimal.ONE, PLN);
        Money czkFromPln = new Money(new BigDecimal("5.5"), CZK);
        Money czkBase = new Money(BigDecimal.ONE, CZK);
        Money hufFromCzk = new Money(new BigDecimal("7.0"), HUF);
        Money hufBase = new Money(BigDecimal.ONE, HUF);
        Money ronFromHuf = new Money(new BigDecimal("0.012"), RON);
        Money ronBase = new Money(BigDecimal.ONE, RON);
        Money usdFromRon = new Money(new BigDecimal("0.22"), USD);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(
            new ExchangeRate(eurBase, plnFromEur),
            new ExchangeRate(plnBase, czkFromPln),
            new ExchangeRate(czkBase, hufFromCzk),
            new ExchangeRate(hufBase, ronFromHuf),
            new ExchangeRate(ronBase, usdFromRon)
        ));
        
        // EUR->USD requires 5 hops, should fail
        assertThatThrownBy(() -> converter.getExchangeRate(EUR, USD))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("No exchange rate path found");
    }

    @Test
    public void exactlyFourHopsWorks() {
        // Verify that exactly 4 hops works (boundary case)
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money plnFromEur = new Money(new BigDecimal("4.5"), PLN);
        Money plnBase = new Money(BigDecimal.ONE, PLN);
        Money czkFromPln = new Money(new BigDecimal("5.5"), CZK);
        Money czkBase = new Money(BigDecimal.ONE, CZK);
        Money hufFromCzk = new Money(new BigDecimal("7.0"), HUF);
        Money hufBase = new Money(BigDecimal.ONE, HUF);
        Money usdFromHuf = new Money(new BigDecimal("0.0025"), USD);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(
            new ExchangeRate(eurBase, plnFromEur),
            new ExchangeRate(plnBase, czkFromPln),
            new ExchangeRate(czkBase, hufFromCzk),
            new ExchangeRate(hufBase, usdFromHuf)
        ));
        
        // EUR->USD requires exactly 4 hops, should work
        ExchangeRate rate = converter.getExchangeRate(EUR, USD);
        // 4.5 * 5.5 * 7.0 * 0.0025 = 0.433125
        assertThat(rate.getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("0.433125"));
    }
}
