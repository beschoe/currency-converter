/*
 * Created on 17 Oct 2025
 *
 * Tests for INC-3: Synthetic cross-rate calculation with arbitrary currency pairs
 */
package com.mercateo.common.currency;

import static com.mercateo.common.currency.ConvertableCurrency.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.Test;

public class SyntheticRatesTest {

    @Test
    public void directRate_shouldBeUsed() {
        // Given: Direct USD -> EUR rate
        Money usdBase = new Money(BigDecimal.ONE, USD);
        Money eurQuote = new Money(new BigDecimal("0.92"), EUR);
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(new ExchangeRate(usdBase, eurQuote))
        );

        // When: Converting USD to EUR
        Money result = converter.convertToPrice(
            new Money(new BigDecimal("100.00"), USD), EUR
        );

        // Then: Should use direct rate
        assertThat(result).isEqualTo(new Money(new BigDecimal("92.00"), EUR));
    }

    @Test
    public void syntheticRate_twoHops_shouldBeCalculated() {
        // Given: USD -> EUR -> GBP chain (no direct USD -> GBP)
        Money usdOne = new Money(BigDecimal.ONE, USD);
        Money eurFromUsd = new Money(new BigDecimal("0.92"), EUR);
        Money eurOne = new Money(BigDecimal.ONE, EUR);
        Money gbpFromEur = new Money(new BigDecimal("0.84"), GBP);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(
                new ExchangeRate(usdOne, eurFromUsd),
                new ExchangeRate(eurOne, gbpFromEur)
            )
        );

        // When: Converting USD to GBP (requires synthetic rate)
        ExchangeRate syntheticRate = converter.getExchangeRate(USD, GBP);
        
        // Then: Should calculate synthetic rate USD -> EUR -> GBP
        // 1 USD = 0.92 EUR, 1 EUR = 0.84 GBP, so 1 USD = 0.92 * 0.84 = 0.7728 GBP
        assertThat(syntheticRate.getRateValue().getAmount())
            .isEqualByComparingTo(new BigDecimal("0.7728"));
    }

    @Test
    public void syntheticRate_threeHops_shouldBeCalculated() {
        // Given: USD -> EUR -> GBP -> CHF chain
        Money usdOne = new Money(BigDecimal.ONE, USD);
        Money eurFromUsd = new Money(new BigDecimal("0.90"), EUR);
        Money eurOne = new Money(BigDecimal.ONE, EUR);
        Money gbpFromEur = new Money(new BigDecimal("0.85"), GBP);
        Money gbpOne = new Money(BigDecimal.ONE, GBP);
        Money chfFromGbp = new Money(new BigDecimal("1.10"), CHF);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(
                new ExchangeRate(usdOne, eurFromUsd),
                new ExchangeRate(eurOne, gbpFromEur),
                new ExchangeRate(gbpOne, chfFromGbp)
            )
        );

        // When: Converting USD to CHF (requires 3-hop synthetic rate)
        ExchangeRate syntheticRate = converter.getExchangeRate(USD, CHF);
        
        // Then: Should calculate 3-hop rate: 0.90 * 0.85 * 1.10 = 0.8415
        assertThat(syntheticRate.getRateValue().getAmount())
            .isEqualByComparingTo(new BigDecimal("0.8415"));
    }

    @Test
    public void syntheticRate_fourHops_shouldBeCalculated() {
        // Given: USD -> EUR -> GBP -> CHF -> SEK chain (4 hops)
        Money usdOne = new Money(BigDecimal.ONE, USD);
        Money eurFromUsd = new Money(new BigDecimal("0.90"), EUR);
        Money eurOne = new Money(BigDecimal.ONE, EUR);
        Money gbpFromEur = new Money(new BigDecimal("0.85"), GBP);
        Money gbpOne = new Money(BigDecimal.ONE, GBP);
        Money chfFromGbp = new Money(new BigDecimal("1.10"), CHF);
        Money chfOne = new Money(BigDecimal.ONE, CHF);
        Money sekFromChf = new Money(new BigDecimal("11.0"), SEK);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(
                new ExchangeRate(usdOne, eurFromUsd),
                new ExchangeRate(eurOne, gbpFromEur),
                new ExchangeRate(gbpOne, chfFromGbp),
                new ExchangeRate(chfOne, sekFromChf)
            )
        );

        // When: Converting USD to SEK (requires 4-hop synthetic rate)
        ExchangeRate syntheticRate = converter.getExchangeRate(USD, SEK);
        
        // Then: Should calculate 4-hop rate: 0.90 * 0.85 * 1.10 * 11.0 = 9.2565
        assertThat(syntheticRate.getRateValue().getAmount())
            .isEqualByComparingTo(new BigDecimal("9.2565"));
    }

    @Test
    public void noPath_shouldThrowIllegalArgumentException() {
        // Given: USD -> EUR, but GBP is isolated (no path from USD to GBP)
        Money usdOne = new Money(BigDecimal.ONE, USD);
        Money eurFromUsd = new Money(new BigDecimal("0.92"), EUR);
        Money gbpOne = new Money(BigDecimal.ONE, GBP);
        Money gbpSelf = new Money(BigDecimal.ONE, GBP);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(
                new ExchangeRate(usdOne, eurFromUsd),
                new ExchangeRate(gbpOne, gbpSelf)
            )
        );

        // When/Then: Should throw IllegalArgumentException for no path
        assertThatThrownBy(() -> converter.getExchangeRate(USD, GBP))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("No exchange rate path found");
    }

    @Test
    public void directRatePrecedence_overSynthetic() {
        // Given: Both direct USD -> GBP and synthetic via EUR exist
        Money usdOne = new Money(BigDecimal.ONE, USD);
        Money gbpDirect = new Money(new BigDecimal("0.77"), GBP); // Direct rate
        Money eurFromUsd = new Money(new BigDecimal("0.92"), EUR);
        Money eurOne = new Money(BigDecimal.ONE, EUR);
        Money gbpFromEur = new Money(new BigDecimal("0.84"), GBP); // Synthetic would give 0.7728
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(
                new ExchangeRate(usdOne, gbpDirect),      // Direct
                new ExchangeRate(usdOne, eurFromUsd),     // Synthetic path
                new ExchangeRate(eurOne, gbpFromEur)      // Synthetic path
            )
        );

        // When: Getting exchange rate
        ExchangeRate rate = converter.getExchangeRate(USD, GBP);
        
        // Then: Should use direct rate (0.77), not synthetic (0.7728)
        assertThat(rate.getRateValue().getAmount())
            .isEqualByComparingTo(new BigDecimal("0.77"));
    }

    @Test
    public void shortestPath_shouldBeSelected_whenMultiplePaths() {
        // Given: Two paths from USD to SEK:
        //   Path 1 (2 hops): USD -> EUR -> SEK
        //   Path 2 (3 hops): USD -> GBP -> CHF -> SEK
        Money usdOne = new Money(BigDecimal.ONE, USD);
        Money eurFromUsd = new Money(new BigDecimal("0.90"), EUR);
        Money gbpFromUsd = new Money(new BigDecimal("0.77"), GBP);
        
        Money eurOne = new Money(BigDecimal.ONE, EUR);
        Money sekFromEur = new Money(new BigDecimal("11.0"), SEK);
        
        Money gbpOne = new Money(BigDecimal.ONE, GBP);
        Money chfFromGbp = new Money(new BigDecimal("1.20"), CHF);
        Money chfOne = new Money(BigDecimal.ONE, CHF);
        Money sekFromChf = new Money(new BigDecimal("10.5"), SEK);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(
                // Shorter path (2 hops)
                new ExchangeRate(usdOne, eurFromUsd),
                new ExchangeRate(eurOne, sekFromEur),
                // Longer path (3 hops)
                new ExchangeRate(usdOne, gbpFromUsd),
                new ExchangeRate(gbpOne, chfFromGbp),
                new ExchangeRate(chfOne, sekFromChf)
            )
        );

        // When: Getting exchange rate
        ExchangeRate rate = converter.getExchangeRate(USD, SEK);
        
        // Then: Should use shorter path (2 hops): 0.90 * 11.0 = 9.9
        // NOT longer path (3 hops): 0.77 * 1.20 * 10.5 = 9.702
        assertThat(rate.getRateValue().getAmount())
            .isEqualByComparingTo(new BigDecimal("9.9"));
    }

    @Test
    public void sameCurrency_shouldReturnIdentityRate() {
        // Given: Converter with some rates
        Money usdOne = new Money(BigDecimal.ONE, USD);
        Money eurFromUsd = new Money(new BigDecimal("0.92"), EUR);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(new ExchangeRate(usdOne, eurFromUsd))
        );

        // When: Getting same-currency rate
        ExchangeRate rate = converter.getExchangeRate(USD, USD);
        
        // Then: Should return identity rate (1.0)
        assertThat(rate.getRateValue().getAmount()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(rate.getBaseCurrency()).isEqualTo(USD);
        assertThat(rate.getQuoteCurrency()).isEqualTo(USD);
    }

    @Test
    public void conversionWithSyntheticRate_shouldMaintainPrecision() {
        // Given: Synthetic rate via 2-hop path
        Money usdOne = new Money(BigDecimal.ONE, USD);
        Money eurFromUsd = new Money(new BigDecimal("0.92"), EUR);
        Money eurOne = new Money(BigDecimal.ONE, EUR);
        Money gbpFromEur = new Money(new BigDecimal("0.84"), GBP);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(
                new ExchangeRate(usdOne, eurFromUsd),
                new ExchangeRate(eurOne, gbpFromEur)
            )
        );

        // When: Converting with TO_PRICE strategy
        Money amount = new Money(new BigDecimal("100.00"), USD);
        Money result = converter.convertToPrice(amount, GBP);
        
        // Then: Should apply proper rounding: 100 * 0.7728 = 77.28 GBP
        assertThat(result).isEqualTo(new Money(new BigDecimal("77.28"), GBP));
    }

    @Test
    public void bidirectionalPairs_shouldWorkCorrectly() {
        // Given: Both USD -> EUR and EUR -> USD rates
        Money usdOne = new Money(BigDecimal.ONE, USD);
        Money eurFromUsd = new Money(new BigDecimal("0.92"), EUR);
        Money eurOne = new Money(BigDecimal.ONE, EUR);
        Money usdFromEur = new Money(new BigDecimal("1.09"), USD);
        
        FrozenCurrencyConverter converter = new FrozenCurrencyConverter(
            asList(
                new ExchangeRate(usdOne, eurFromUsd),
                new ExchangeRate(eurOne, usdFromEur)
            )
        );

        // When/Then: Both directions should work
        assertThat(converter.getExchangeRate(USD, EUR).getRateValue().getAmount())
            .isEqualByComparingTo(new BigDecimal("0.92"));
        assertThat(converter.getExchangeRate(EUR, USD).getRateValue().getAmount())
            .isEqualByComparingTo(new BigDecimal("1.09"));
    }
}
