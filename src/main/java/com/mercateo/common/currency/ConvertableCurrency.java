package com.mercateo.common.currency;
import java.math.RoundingMode;
import java.util.Currency;

import com.mercateo.common.i18n.KnownCurrencies;
import com.mercateo.common.util.annotations.NonNullByDefault;

/**
 * This interface contains some supported ISO 4217 currency codes and currency
 * instances.
 *
 * @author rene.mazala
 */
@NonNullByDefault
public enum ConvertableCurrency {
    /**
     * The Euro
     */
    EUR,

    /**
     * The US Dollar
     */
    USD,

    /**
     * The Great British Pound Sterling
     */
    GBP,

    /**
     * The Polish Zloty
     */
    PLN,

    /**
     * The Czech Koruna
     */
    CZK,

    /**
     * The Swiss Franc
     */
    CHF,

    /**
     * The Danish Krone
     */
    DKK,

    /**
     * The Croatian Kuna
     */
    HRK,

    /**
     * The Swedish Krona
     */
    SEK,

    /**
     * The Bulgarian Lev
     */
    BGN,

    /**
     * The Hungarian Forint
     */
    HUF,

    /**
     * The Latvian Lats
     */
    LVL,

    /**
     * The Lithuanian Litas
     */
    LTL,

    /**
     * The Romanian Leu
     */
    RON,

    /**
     * The Turkish Lira
     */
    TRY,

    /**
     * The "Deutsche Mark" from Germany before "EUR"-Time
     */
    DEM,

    /**
     * Chinese yuan
     */
    CNY,

    /**
     * Indian Rupee
     */
    INR,

    /**
     * Brazilian Real
     */
    BRL,

    /**
     * Mexican Peso
     */
    MXN;

    @SuppressWarnings("null")
    public static ConvertableCurrency valueOf(Currency currency) {
        KnownCurrencies fromCurrency = KnownCurrencies.parseFromCurrency(currency);
        return valueOf(fromCurrency);
    }

    public static ConvertableCurrency valueOf(KnownCurrencies fromCurrency) {
        return values()[fromCurrency.ordinal()];
    }

    public final Currency currency;

    public int getDefaultScale() {
        return currency == HUF.currency ? 0 : currency.getDefaultFractionDigits();
    }

    public RoundingMode getRoundingMode() {
        return currency == HUF.currency ? RoundingMode.UP : RoundingMode.HALF_EVEN;
    }

    private ConvertableCurrency() {
        this.currency = Currency.getInstance(this.name());
    }

    public String getCurrencyCode() {
        return this.name();
    }

}
