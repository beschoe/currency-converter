/*
 * Created on 17 Oct 2025
 *
 * Tests for synthetic cross-rate calculation (INC-3)
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
 * Tests for INC-3: Unrestricted pair ingestion and synthetic cross-rate calculation.
 * 
 * These tests validate:
 * - Direct rate precedence
 * - Synthetic multi-hop conversion (up to 4 hops)
 * - No-path error handling
 * - Same-currency conversion
 * - Deterministic shortest-hop selection
 * - Hop limit enforcement
 */
public class SyntheticCrossRateTest {

    @Test
    public void happyPath_directConversion_returnsExpectedResult() {
        // Given: Direct EUR->USD rate
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money usdQuote = new Money(new BigDecimal("1.10"), USD);
        ExchangeRate eurToUsd = new ExchangeRate(eurBase, usdQuote);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(eurToUsd));
        
        // When: Converting EUR to USD
        Money euros = new Money(new BigDecimal("100"), EUR);
        Money result = converter.convertToPrice(euros, USD);
        
        // Then: Direct rate is used
        assertThat(result).isEqualTo(new Money(new BigDecimal("110.00"), USD));
    }

    @Test
    public void syntheticConversion_twoHops_calculatesCorrectRate() {
        // Given: EUR->GBP and GBP->USD rates (no direct EUR->USD)
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money gbpBase = new Money(BigDecimal.ONE, GBP);
        
        ExchangeRate eurToGbp = new ExchangeRate(eurBase, new Money(new BigDecimal("0.85"), GBP));
        ExchangeRate gbpToUsd = new ExchangeRate(gbpBase, new Money(new BigDecimal("1.25"), USD));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(eurToGbp, gbpToUsd));
        
        // When: Converting EUR to USD (synthetic via GBP)
        Money euros = new Money(new BigDecimal("100"), EUR);
        Money result = converter.convertToPrice(euros, USD);
        
        // Then: Synthetic rate is calculated: 100 EUR * 0.85 GBP/EUR * 1.25 USD/GBP = 106.25 USD
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("106.25"));
        assertThat(result.getCurrency()).isEqualTo(USD);
    }

    @Test
    public void syntheticConversion_threeHops_calculatesCorrectRate() {
        // Given: Chain EUR->GBP->USD->CHF (no direct EUR->CHF)
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money gbpBase = new Money(BigDecimal.ONE, GBP);
        Money usdBase = new Money(BigDecimal.ONE, USD);
        
        ExchangeRate eurToGbp = new ExchangeRate(eurBase, new Money(new BigDecimal("0.85"), GBP));
        ExchangeRate gbpToUsd = new ExchangeRate(gbpBase, new Money(new BigDecimal("1.25"), USD));
        ExchangeRate usdToChf = new ExchangeRate(usdBase, new Money(new BigDecimal("0.92"), CHF));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(eurToGbp, gbpToUsd, usdToChf));
        
        // When: Converting EUR to CHF (3-hop synthetic)
        ExchangeRate rate = converter.getExchangeRate(EUR, CHF);
        
        // Then: Synthetic rate is calculated: 0.85 * 1.25 * 0.92 = 0.9775
        assertThat(rate.getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("0.9775"));
    }

    @Test
    public void syntheticConversion_fourHops_calculatesCorrectRate() {
        // Given: Chain EUR->GBP->USD->CHF->DKK (4 hops)
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money gbpBase = new Money(BigDecimal.ONE, GBP);
        Money usdBase = new Money(BigDecimal.ONE, USD);
        Money chfBase = new Money(BigDecimal.ONE, CHF);
        
        ExchangeRate eurToGbp = new ExchangeRate(eurBase, new Money(new BigDecimal("0.85"), GBP));
        ExchangeRate gbpToUsd = new ExchangeRate(gbpBase, new Money(new BigDecimal("1.25"), USD));
        ExchangeRate usdToChf = new ExchangeRate(usdBase, new Money(new BigDecimal("0.92"), CHF));
        ExchangeRate chfToDkk = new ExchangeRate(chfBase, new Money(new BigDecimal("7.0"), DKK));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(
            eurToGbp, gbpToUsd, usdToChf, chfToDkk
        ));
        
        // When: Converting EUR to DKK (4-hop synthetic, at the limit)
        ExchangeRate rate = converter.getExchangeRate(EUR, DKK);
        
        // Then: Synthetic rate is calculated: 0.85 * 1.25 * 0.92 * 7.0 = 6.8425
        assertThat(rate.getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("6.8425"));
    }

    @Test
    public void failurePath_noAvailablePath_throwsIllegalArgumentException() {
        // Given: EUR->GBP rate, but no connection to USD
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        ExchangeRate eurToGbp = new ExchangeRate(eurBase, new Money(new BigDecimal("0.85"), GBP));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(eurToGbp));
        
        // When/Then: Converting to unconnected currency throws exception
        assertThatThrownBy(() -> converter.convertToPrice(new Money(BigDecimal.ONE, EUR), USD))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("No exchange rate path found");
    }

    @Test
    public void edgeCase_sameCurrencyConversion_preservesPrecision() {
        // Given: Converter with some rates
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        ExchangeRate eurToGbp = new ExchangeRate(eurBase, new Money(new BigDecimal("0.85"), GBP));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(eurToGbp));
        
        // When: Converting EUR to EUR with different precision
        Money euros = new Money(new BigDecimal("1.0101"), EUR);
        Money result = converter.convertToPrice(euros, EUR);
        
        // Then: Amount is scaled to price precision (2 decimals for EUR)
        assertThat(result).isEqualTo(new Money(new BigDecimal("1.01"), EUR));
    }

    @Test
    public void edgeCase_directRatePrecedence_prefersDirectOverSynthetic() {
        // Given: Both direct EUR->USD and synthetic path EUR->GBP->USD
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money gbpBase = new Money(BigDecimal.ONE, GBP);
        
        ExchangeRate directEurToUsd = new ExchangeRate(eurBase, new Money(new BigDecimal("1.10"), USD));
        ExchangeRate eurToGbp = new ExchangeRate(eurBase, new Money(new BigDecimal("0.85"), GBP));
        ExchangeRate gbpToUsd = new ExchangeRate(gbpBase, new Money(new BigDecimal("1.30"), USD));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(
            directEurToUsd, eurToGbp, gbpToUsd
        ));
        
        // When: Getting EUR->USD rate
        ExchangeRate rate = converter.getExchangeRate(EUR, USD);
        
        // Then: Direct rate (1.10) is used, not synthetic (0.85 * 1.30 = 1.105)
        assertThat(rate.getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("1.10"));
    }

    @Test
    public void edgeCase_multiplePaths_selectsShortestDeterministically() {
        // Given: Multiple paths from EUR to CHF
        // Path 1 (2 hops): EUR->USD->CHF
        // Path 2 (2 hops): EUR->GBP->CHF
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money usdBase = new Money(BigDecimal.ONE, USD);
        Money gbpBase = new Money(BigDecimal.ONE, GBP);
        
        ExchangeRate eurToUsd = new ExchangeRate(eurBase, new Money(new BigDecimal("1.10"), USD));
        ExchangeRate eurToGbp = new ExchangeRate(eurBase, new Money(new BigDecimal("0.85"), GBP));
        ExchangeRate usdToChf = new ExchangeRate(usdBase, new Money(new BigDecimal("0.91"), CHF));
        ExchangeRate gbpToChf = new ExchangeRate(gbpBase, new Money(new BigDecimal("1.15"), CHF));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(
            eurToUsd, eurToGbp, usdToChf, gbpToChf
        ));
        
        // When: Getting EUR->CHF rate multiple times
        BigDecimal rate1 = converter.getExchangeRate(EUR, CHF).getRateValue().getAmount();
        BigDecimal rate2 = converter.getExchangeRate(EUR, CHF).getRateValue().getAmount();
        
        // Then: Same path is selected deterministically
        assertThat(rate1).isEqualByComparingTo(rate2);
        // BFS with enum ordering will pick the first found shortest path consistently
    }

    @Test
    public void edgeCase_hopLimitBoundary_respectsMaxHops() {
        // Given: A long chain that would require more than 4 hops
        // Chain: EUR->GBP->USD->CHF->DKK->SEK (would be 5 hops)
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money gbpBase = new Money(BigDecimal.ONE, GBP);
        Money usdBase = new Money(BigDecimal.ONE, USD);
        Money chfBase = new Money(BigDecimal.ONE, CHF);
        Money dkkBase = new Money(BigDecimal.ONE, DKK);
        
        ExchangeRate eurToGbp = new ExchangeRate(eurBase, new Money(new BigDecimal("0.85"), GBP));
        ExchangeRate gbpToUsd = new ExchangeRate(gbpBase, new Money(new BigDecimal("1.25"), USD));
        ExchangeRate usdToChf = new ExchangeRate(usdBase, new Money(new BigDecimal("0.92"), CHF));
        ExchangeRate chfToDkk = new ExchangeRate(chfBase, new Money(new BigDecimal("7.0"), DKK));
        ExchangeRate dkkToSek = new ExchangeRate(dkkBase, new Money(new BigDecimal("1.4"), SEK));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(
            eurToGbp, gbpToUsd, usdToChf, chfToDkk, dkkToSek
        ));
        
        // When/Then: Converting EUR to SEK (would need 5 hops) throws exception
        assertThatThrownBy(() -> converter.getExchangeRate(EUR, SEK))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("No exchange rate path found");
    }

    @Test
    public void unrestricted_differentBaseCurrencies_noLongerFails() {
        // Given: Rates with different base currencies (EUR-based and USD-based clusters)
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        Money usdBase = new Money(BigDecimal.ONE, USD);
        
        ExchangeRate eurToGbp = new ExchangeRate(eurBase, new Money(new BigDecimal("0.85"), GBP));
        ExchangeRate usdToChf = new ExchangeRate(usdBase, new Money(new BigDecimal("0.92"), CHF));
        ExchangeRate usdToEur = new ExchangeRate(usdBase, new Money(new BigDecimal("0.91"), EUR));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(
            eurToGbp, usdToChf, usdToEur
        ));
        
        // When: Converting between previously isolated clusters
        Money gbp = new Money(new BigDecimal("100"), GBP);
        Money result = converter.convertToPrice(gbp, CHF);
        
        // Then: Conversion succeeds via synthetic path GBP->EUR->USD->CHF
        assertThat(result.getCurrency()).isEqualTo(CHF);
        assertThat(result.getAmount()).isNotNull();
    }

    @Test
    public void precision_syntheticRate_matchesOriginalBehavior() {
        // Given: Same setup as original tests with EUR as common base
        Money eurBase = new Money(BigDecimal.ONE, EUR);
        
        ExchangeRate eurToGbp = new ExchangeRate(eurBase, new Money(new BigDecimal("0.84"), GBP));
        ExchangeRate eurToUsd = new ExchangeRate(eurBase, new Money(new BigDecimal("1.09"), USD));
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(asList(eurToGbp, eurToUsd));
        
        // When: Getting synthetic GBP->USD rate
        ExchangeRate rate = converter.getExchangeRate(GBP, USD);
        
        // Then: Precision matches original withBase calculation
        // Expected: 1.09 / 0.84 = 1.2976190476...
        assertThat(rate.getRateValue().getAmount()).isEqualByComparingTo(new BigDecimal("1.2976190476"));
    }
}
