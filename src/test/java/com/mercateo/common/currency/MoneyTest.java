/*
 * Created on 25 Jun 2025
 *
 * author dimitry
 */
package com.mercateo.common.currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.Test;


@SuppressWarnings("null")
public class MoneyTest {

    @Test
    public void testCompareToEqualAmounts() {
        ConvertableCurrency currency = ConvertableCurrency.EUR;
        Money money1 = new Money(new BigDecimal("100.00"), currency);
        Money money2 = new Money(new BigDecimal("100.00"), currency);

        assertThat(money1.compareTo(money2)).isEqualTo(0);
        assertThat(money2.compareTo(money1)).isEqualTo(0);
    }

    @Test
    public void testCompareToFirstAmountGreater() {
        ConvertableCurrency currency = ConvertableCurrency.EUR;
        Money money1 = new Money(new BigDecimal("150.00"), currency);
        Money money2 = new Money(new BigDecimal("100.00"), currency);

        assertThat(money1.compareTo(money2)).isPositive();
        assertThat(money2.compareTo(money1)).isNegative();
    }

    @Test
    public void testCompareToFirstAmountSmaller() {
        ConvertableCurrency currency = ConvertableCurrency.USD;
        Money money1 = new Money(new BigDecimal("75.00"), currency);
        Money money2 = new Money(new BigDecimal("100.00"), currency);

        assertThat(money1.compareTo(money2)).isNegative();
        assertThat(money2.compareTo(money1)).isPositive();
    }

    @Test
    public void testCompareToWithZeroAmounts() {
        ConvertableCurrency currency = ConvertableCurrency.EUR;
        Money zeroMoney1 = new Money(BigDecimal.ZERO, currency);
        Money zeroMoney2 = new Money(BigDecimal.ZERO, currency);
        Money positiveMoney = new Money(new BigDecimal("50.00"), currency);

        assertThat(zeroMoney1.compareTo(zeroMoney2)).isEqualTo(0);
        assertThat(positiveMoney.compareTo(zeroMoney1)).isPositive();
        assertThat(zeroMoney1.compareTo(positiveMoney)).isNegative();
    }

    @Test
    public void testCompareToWithDifferentCurrencies() {
        Money eurMoney = new Money(new BigDecimal("100.00"), ConvertableCurrency.EUR);
        Money usdMoney = new Money(new BigDecimal("100.00"), ConvertableCurrency.USD);

        assertThatThrownBy(() -> eurMoney.compareTo(usdMoney))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Can't compare monetary values with different currencies")
            .hasMessageContaining("EUR")
            .hasMessageContaining("USD");
    }
}
