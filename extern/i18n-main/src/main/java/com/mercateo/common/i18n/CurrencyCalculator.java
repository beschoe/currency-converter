package com.mercateo.common.i18n;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import com.mercateo.common.util.annotations.NonNullByDefault;

/**
 * Created by till on 19.11.15.
 */
@NonNullByDefault
public class CurrencyCalculator {
    /**
     * NOTE: there is {@link KnownCurrencies#round(BigDecimal, Currency)}, but
     * ITD-22399 requests {@link RoundingMode#UP}
     * 
     * @param amount
     * @param currencyRate
     * @param targetCurrency
     * @return the currency changed amount with the scale as in amount (rounded
     *         HALF_EVEN), or scale (rounded UP) if the target currency is HUF
     */
    public static BigDecimal applyCurrencyRate(BigDecimal amount, BigDecimal currencyRate,
            Currency targetCurrency) {
        if (KnownCurrencies.determineDefaultScaleForCurrency(targetCurrency) == 0) {
            return amount.multiply(currencyRate).setScale(0, RoundingMode.UP);
        } else {
            return amount.multiply(currencyRate).setScale(amount.scale(), RoundingMode.HALF_EVEN);
        }
    }

    /**
     * @param amount
     * @param currencyRate
     * @return the currency changed amount with maximum number of significant
     *         digits
     */
    public static BigDecimal applyCurrencyRateExact(BigDecimal amount, BigDecimal currencyRate) {
        return amount.multiply(currencyRate);
    }
}
