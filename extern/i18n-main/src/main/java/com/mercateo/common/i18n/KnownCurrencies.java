package com.mercateo.common.i18n;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mercateo.common.util.ThreadLogger;
import com.mercateo.common.util.annotations.NonNullByDefault;
import com.mercateo.common.util.annotations.Nullable;

/**
 * This interface contains some supported ISO 4217 currency codes and currency
 * instances.
 * 
 * @author rene.mazala
 */
@NonNullByDefault
public enum KnownCurrencies {
    /**
     * The Euro
     */
    EUR("&euro;"),

    /**
     * The US Dollar
     */
    USD,

    /**
     * The Great British Pound Sterling
     */
    GBP("&pound;"),

    /**
     * The Polish Zloty
     */
    PLN,

    /**
     * The Czech Koruna
     */
    CZK("K&#x010D;"),

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

    private static final int HIGH_PRECISION_SCALE_ADDEND = 3;
    public final Currency currency;

    public final String currencyHtml;

    private KnownCurrencies() {
        // use the ISO code as displayed symbol
        this(null);
    }

    private KnownCurrencies(@Nullable String currencyHtml) {
        this.currency = Currency.getInstance(this.name());
        if (currencyHtml != null) {
            this.currencyHtml = currencyHtml;
        } else {
            // use the ISO code as displayed symbol
            this.currencyHtml = this.name();
        }
    }

    private static final Map<String, KnownCurrencies> STRING_TO_KNOWNCURRENCIES_MAP = Collections
            .unmodifiableMap(new HashMap<String, KnownCurrencies>() {
                {
                    for (KnownCurrencies knownCurrencies : KnownCurrencies.values()) {
                        put(knownCurrencies.currency.getCurrencyCode(), knownCurrencies);
                    }
                }
            });

    public static Set<String> getSupportedCurrencyCodes() {
        return STRING_TO_KNOWNCURRENCIES_MAP.keySet();
    }

    @Nullable
    public static KnownCurrencies parse(String currencyString) {
        return STRING_TO_KNOWNCURRENCIES_MAP.get(currencyString);
    }

    public String getCurrencyCode() {
        return this.name();
    }

    private static final Map<Currency, KnownCurrencies> CURRENCY_TO_KNOWNCURRENCIES_MAP = Collections
            .unmodifiableMap(new HashMap<Currency, KnownCurrencies>() {
                {
                    for (KnownCurrencies knownCurrencies : KnownCurrencies.values()) {
                        put(knownCurrencies.currency, knownCurrencies);
                    }
                }
            });

    @Nullable
    public static KnownCurrencies parseFromCurrency(Currency currency) {
        return CURRENCY_TO_KNOWNCURRENCIES_MAP.get(currency);
    }

    public static String determineCurrencyHtmlFromCurrency(@Nullable Currency currency) {
        if (currency == null) {
            /*
             * no paranoia: used e.g. for content articles with keepContent.1
             */
            return determineCurrencyHtmlFromCurrencyCode(null);
        }
        return determineCurrencyHtmlFromCurrencyCode(currency.getCurrencyCode());
    }

    public static String determineCurrencyHtmlFromCurrencyCode(
            @Nullable final String currencyCode) {
        if (currencyCode == null) {
            /*
             * no paranoia: used e.g. for content articles with keepContent.1
             */
            return "";
        }
        final KnownCurrencies parsedKnownCurrency = parse(currencyCode);
        if (parsedKnownCurrency != null) {
            return parsedKnownCurrency.currencyHtml;
        }
        ThreadLogger.warning("Unsupported currency code " + currencyCode, true);
        // use the currencyCode as fallback
        return currencyCode;
    }

    public static BigDecimal roundWithHighPrecision(BigDecimal money, Currency currency) {
        return money.setScale(getHighPrecisionScaleForCurrency(currency), RoundingMode.HALF_UP);
    }

    public static int determineHighPrecisionScaleForCurrency(Currency currency) {
        return getHighPrecisionScaleForCurrency(currency);
    }

    private static int getHighPrecisionScaleForCurrency(Currency currency) {
        return determineDefaultScaleForCurrency(currency) + HIGH_PRECISION_SCALE_ADDEND;
    }

    /**
     * NOTE: this currently not used by
     * {@link com.mercateo.common.i18n.CurrencyCalculator#applyCurrencyRate(BigDecimal, BigDecimal, Currency)}
     *
     * @param money
     * @param currency
     * @return BigDecimal
     */
    public static BigDecimal round(BigDecimal money, Currency currency) {
        return money.setScale(determineDefaultScaleForCurrency(currency), RoundingMode.HALF_UP);
    }

    /**
     * @param currency
     * @return 0 or 2 (NOTE: if the possible values are changed, check places
     *         like formatter patterns)
     */
    public static int determineDefaultScaleForCurrency(@Nullable Currency currency) {
        if (currency == HUF.currency) {
            // in hungary, "filler=1/100 HUF" is still in java, but not used any
            // longer
            return 0;
        } else if (currency != null) {
            return currency.getDefaultFractionDigits();
        } else {
            return 2;
        }
    }
}
