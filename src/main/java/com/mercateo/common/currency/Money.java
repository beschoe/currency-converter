/*
 * Created on 19 Mar 2025
 *
 * author dimitry
 */
package com.mercateo.common.currency;

import java.math.BigDecimal;
import java.util.Objects;

import com.mercateo.common.util.annotations.Nullable;

/**
 * Immutable class representing a monetary amount in a specific currency.
 *
 * This class handles precise monetary calculations and can be converted
 * to different currencies when combined with appropriate exchange rates.
 */
public class Money {
    private final BigDecimal amount;
    private final ConvertableCurrency currency;

    /**
     * Creates a new Money object with the specified amount and currency.
     *
     * @param amount The monetary amount as a BigDecimal
     * @param currency The currency of the monetary amount
     */
    public Money(BigDecimal amount, ConvertableCurrency currency) {
        super();
        this.amount = amount;
        this.currency = currency;
    }

    /**
     * Gets the monetary amount.
     *
     * @return The monetary amount as a BigDecimal
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Gets the currency of this monetary amount.
     *
     * @return The currency
     */
    public ConvertableCurrency getCurrency() {
        return currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, amount);
    }
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Money other = (Money) obj;
        return currency == other.currency && Objects.equals(amount, other.amount);
    }
    @Override
    public String toString() {
        return "Money [amount=" + amount + ", currency=" + currency + "]";
    }


}
