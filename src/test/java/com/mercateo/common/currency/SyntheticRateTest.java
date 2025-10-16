/*
 * Test cases for synthetic rate calculation with unrestricted currency pairs
 */
package com.mercateo.common.currency;

import static com.mercateo.common.currency.ConvertableCurrency.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

/**
 * Tests for INC-3: Lifting canonical base currency constraint.
 * Validates synthetic cross-rate calculation, path finding, and edge cases.
 */
public class SyntheticRateTest {

    /**
     * Test: Happy path - direct conversion with ingested pair returns expected result
     */
    @Test
    public void testDirectConversion() {
        Money usdBase = new Money(BigDecimal.ONE, USD);
        Money eurQuote = new Money(new BigDecimal("0.92"), EUR);
        ExchangeRate usdToEur = new ExchangeRate(usdBase, eurQuote);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(usdToEur));
        
        Money amount = new Money(new BigDecimal("100.00"), USD);
        Money converted = converter.convertToPrice(amount, EUR);
        
        assertThat(converted).isEqualTo(new Money(new BigDecimal("92.00"), EUR));
    }

    /**
     * Test: Synthetic conversion - 2-hop path returns expected result
     */
    @Test
    public void testSyntheticTwoHopConversion() {
        // Create a chain: USD -> EUR -> GBP (no direct USD -> GBP)
        Money usdBase = new Money(BigDecimal.ONE, USD);
        Money eurQuote = new Money(new BigDecimal("0.92"), EUR);
        ExchangeRate usdToEur = new ExchangeRate(usdBase, eurQuote);
        
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money gbpQuote = new Money(new BigDecimal("0.84"), GBP);
        ExchangeRate eurToGbp = new ExchangeRate(eurBase, gbpQuote);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(usdToEur, eurToGbp));
        
        // Convert USD -> GBP should go through EUR
        Money amount = new Money(new BigDecimal("100.00"), USD);
        Money converted = converter.convertToPrice(amount, GBP);
        
        // Expected: 100 * 0.92 * 0.84 = 77.28
        assertThat(converted).isEqualTo(new Money(new BigDecimal("77.28"), GBP));
    }

    /**
     * Test: Synthetic conversion - 3-hop path returns expected result
     */
    @Test
    public void testSyntheticThreeHopConversion() {
        // Create a chain: USD -> EUR -> GBP -> CHF
        Money usdToEurRate = new Money(new BigDecimal("0.92"), EUR);
        Money eurToGbpRate = new Money(new BigDecimal("0.84"), GBP);
        Money gbpToChfRate = new Money(new BigDecimal("1.15"), CHF);
        
        ExchangeRate usdToEur = new ExchangeRate(new Money(BigDecimal.ONE, USD), usdToEurRate);
        ExchangeRate eurToGbp = new ExchangeRate(new Money(BigDecimal.ONE, EUR), eurToGbpRate);
        ExchangeRate gbpToChf = new ExchangeRate(new Money(BigDecimal.ONE, GBP), gbpToChfRate);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(usdToEur, eurToGbp, gbpToChf));
        
        Money amount = new Money(new BigDecimal("100.00"), USD);
        Money converted = converter.convertToPrice(amount, CHF);
        
        // Expected: 100 * 0.92 * 0.84 * 1.15 ≈ 88.87
        assertThat(converted.getAmount()).isEqualByComparingTo(new BigDecimal("88.87"));
    }

    /**
     * Test: Synthetic conversion - 4-hop path (boundary) returns expected result
     */
    @Test
    public void testSyntheticFourHopConversion() {
        // Create a chain: USD -> EUR -> GBP -> CHF -> DKK (4 hops)
        ExchangeRate usdToEur = new ExchangeRate(
            new Money(BigDecimal.ONE, USD),
            new Money(new BigDecimal("0.92"), EUR));
        ExchangeRate eurToGbp = new ExchangeRate(
            new Money(BigDecimal.ONE, EUR),
            new Money(new BigDecimal("0.84"), GBP));
        ExchangeRate gbpToChf = new ExchangeRate(
            new Money(BigDecimal.ONE, GBP),
            new Money(new BigDecimal("1.15"), CHF));
        ExchangeRate chfToDkk = new ExchangeRate(
            new Money(BigDecimal.ONE, CHF),
            new Money(new BigDecimal("7.45"), DKK));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(usdToEur, eurToGbp, gbpToChf, chfToDkk));
        
        Money amount = new Money(new BigDecimal("100.00"), USD);
        Money converted = converter.convertToPrice(amount, DKK);
        
        // Expected: 100 * 0.92 * 0.84 * 1.15 * 7.45 ≈ 662.10 (with rounding)
        assertThat(converted.getAmount()).isEqualByComparingTo(new BigDecimal("662.10"));
    }

    /**
     * Test: Failure path - no available path throws IllegalArgumentException
     */
    @Test
    public void testNoPathThrowsException() {
        // Create two disconnected clusters
        ExchangeRate usdToEur = new ExchangeRate(
            new Money(BigDecimal.ONE, USD),
            new Money(new BigDecimal("0.92"), EUR));
        ExchangeRate gbpToChf = new ExchangeRate(
            new Money(BigDecimal.ONE, GBP),
            new Money(new BigDecimal("1.15"), CHF));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(usdToEur, gbpToChf));
        
        Money amount = new Money(new BigDecimal("100.00"), USD);
        assertThatThrownBy(() -> converter.convertToPrice(amount, GBP))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("No exchange rate path found");
    }

    /**
     * Test: Edge case - same currency conversion preserves scale
     */
    @Test
    public void testSameCurrencyConversion() {
        ExchangeRate usdToEur = new ExchangeRate(
            new Money(BigDecimal.ONE, USD),
            new Money(new BigDecimal("0.92"), EUR));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(usdToEur));
        
        // Convert USD to USD with different scales
        Money amount1 = new Money(new BigDecimal("100.00"), USD);
        Money converted1 = converter.convertToPrice(amount1, USD);
        assertThat(converted1).isEqualTo(new Money(new BigDecimal("100.00"), USD));
        
        Money amount2 = new Money(new BigDecimal("100.0000"), USD);
        Money converted2 = converter.convertProportionally(amount2, USD);
        assertThat(converted2).isEqualTo(new Money(new BigDecimal("100.0000"), USD));
    }

    /**
     * Test: Edge case - direct rate takes precedence over synthetic path
     */
    @Test
    public void testDirectRatePrecedence() {
        // Create both direct and synthetic paths: USD -> GBP
        // Direct: USD -> GBP = 0.77
        // Synthetic through EUR: USD -> EUR (0.92) -> GBP (0.84) = 0.7728
        
        ExchangeRate usdToGbpDirect = new ExchangeRate(
            new Money(BigDecimal.ONE, USD),
            new Money(new BigDecimal("0.77"), GBP));
        ExchangeRate usdToEur = new ExchangeRate(
            new Money(BigDecimal.ONE, USD),
            new Money(new BigDecimal("0.92"), EUR));
        ExchangeRate eurToGbp = new ExchangeRate(
            new Money(BigDecimal.ONE, EUR),
            new Money(new BigDecimal("0.84"), GBP));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(usdToGbpDirect, usdToEur, eurToGbp));
        
        Money amount = new Money(new BigDecimal("100.00"), USD);
        Money converted = converter.convertToPrice(amount, GBP);
        
        // Should use direct rate 0.77, not synthetic 0.7728
        assertThat(converted).isEqualTo(new Money(new BigDecimal("77.00"), GBP));
    }

    /**
     * Test: Edge case - multiple paths, shortest hop wins (deterministic)
     */
    @Test
    public void testShortestPathSelection() {
        // Create multiple paths from USD to CHF:
        // Path 1 (2 hops): USD -> EUR -> CHF
        // Path 2 (3 hops): USD -> GBP -> EUR -> CHF
        
        ExchangeRate usdToEur = new ExchangeRate(
            new Money(BigDecimal.ONE, USD),
            new Money(new BigDecimal("0.92"), EUR));
        ExchangeRate eurToChf = new ExchangeRate(
            new Money(BigDecimal.ONE, EUR),
            new Money(new BigDecimal("0.98"), CHF));
        ExchangeRate usdToGbp = new ExchangeRate(
            new Money(BigDecimal.ONE, USD),
            new Money(new BigDecimal("0.77"), GBP));
        ExchangeRate gbpToEur = new ExchangeRate(
            new Money(BigDecimal.ONE, GBP),
            new Money(new BigDecimal("1.19"), EUR));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(usdToEur, eurToChf, usdToGbp, gbpToEur));
        
        ExchangeRate rate = converter.getExchangeRate(USD, CHF);
        
        // Should use 2-hop path: USD -> EUR -> CHF = 0.92 * 0.98 = 0.9016
        assertThat(rate.getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("0.9016"));
    }

    /**
     * Test: Edge case - hop limit boundary, no composition beyond 4 hops
     */
    @Test
    public void testHopLimitEnforced() {
        // Create a chain that would require 5 hops: USD -> EUR -> GBP -> CHF -> DKK -> SEK
        // But also provide a disconnected SEK cluster so there's no valid path within 4 hops
        
        ExchangeRate usdToEur = new ExchangeRate(
            new Money(BigDecimal.ONE, USD),
            new Money(new BigDecimal("0.92"), EUR));
        ExchangeRate eurToGbp = new ExchangeRate(
            new Money(BigDecimal.ONE, EUR),
            new Money(new BigDecimal("0.84"), GBP));
        ExchangeRate gbpToChf = new ExchangeRate(
            new Money(BigDecimal.ONE, GBP),
            new Money(new BigDecimal("1.15"), CHF));
        ExchangeRate chfToDkk = new ExchangeRate(
            new Money(BigDecimal.ONE, CHF),
            new Money(new BigDecimal("7.45"), DKK));
        ExchangeRate dkkToSek = new ExchangeRate(
            new Money(BigDecimal.ONE, DKK),
            new Money(new BigDecimal("1.50"), SEK));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(usdToEur, eurToGbp, gbpToChf, chfToDkk, dkkToSek));
        
        // USD -> SEK requires 5 hops, should fail
        Money amount = new Money(new BigDecimal("100.00"), USD);
        assertThatThrownBy(() -> converter.convertToPrice(amount, SEK))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("No exchange rate path found");
    }

    /**
     * Test: Unrestricted base currencies - multiple different bases work together
     */
    @Test
    public void testUnrestrictedBaseCurrencies() {
        // Create rates with different bases (not all EUR-based)
        ExchangeRate usdToEur = new ExchangeRate(
            new Money(BigDecimal.ONE, USD),
            new Money(new BigDecimal("0.92"), EUR));
        ExchangeRate gbpToUsd = new ExchangeRate(
            new Money(BigDecimal.ONE, GBP),
            new Money(new BigDecimal("1.30"), USD));
        ExchangeRate chfToGbp = new ExchangeRate(
            new Money(BigDecimal.ONE, CHF),
            new Money(new BigDecimal("0.87"), GBP));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(usdToEur, gbpToUsd, chfToGbp));
        
        // Convert CHF to EUR through path: CHF -> GBP -> USD -> EUR
        Money amount = new Money(new BigDecimal("100.00"), CHF);
        Money converted = converter.convertToPrice(amount, EUR);
        
        // Expected: 100 * 0.87 * 1.30 * 0.92 ≈ 104.05
        assertThat(converted.getAmount()).isEqualByComparingTo(new BigDecimal("104.05"));
    }
}
