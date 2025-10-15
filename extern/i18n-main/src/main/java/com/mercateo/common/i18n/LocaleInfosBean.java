/*
 * Created on 09.07.2010
 *
 * author Sandra.Bsiri
 */
package com.mercateo.common.i18n;

import static java.util.Objects.requireNonNull;

import java.util.Currency;
import java.util.Locale;
import java.util.regex.Pattern;

import com.mercateo.common.util.annotations.NonNullByDefault;

/**
 * TODO_SMS implement a JUnit Test to check the plausibility of Locale
 * Definition against the {@link Locale#getAvailableLocales()}
 * 
 * 
 * @author sven.moebius
 * 
 */
@NonNullByDefault
public class LocaleInfosBean {

    public static final Pattern ZIP_PATTERN_de_DE = Pattern.compile("[0-9]{5}");

    /**
     * don't touch this because this pattern is in SAP!
     */
    public static final Pattern ZIP_PATTERN_nl_NL = Pattern.compile("[0-9]{4}[\\s]{1}[a-zA-Z]{2}");

    public static final Pattern ZIP_PATTERN_nl_NL_Register = Pattern.compile(
            "[0-9]{4}[\\s]{0,1}[a-zA-Z]{2}");

    public static final Pattern ZIP_PATTERN_es_ES = Pattern.compile("[0-9]{5}");

    public static final Pattern ZIP_PATTERN_it_IT = Pattern.compile("[0-9]{5}");

    /**
     * don't touch this because this pattern is in SAP!
     */

    public static final Pattern ZIP_PATTERN_cz_CZ = Pattern.compile("[0-9]{3}[\\s]{1}[0-9]{2}");

    public static final Pattern ZIP_PATTERN_cz_CZ_Register = Pattern.compile(
            "[0-9]{3}[\\s]{0,1}[0-9]{2}");

    public static final Pattern ZIP_PATTERN_fr_FR = Pattern.compile("[0-9]{5}");

    public static final Pattern ZIP_PATTERN_pl_PL = Pattern.compile("[0-9]{2}-?[0-9]{3}");

    public static final Pattern ZIP_PATTERN_at_AT = Pattern.compile("[0-9]{4}");

    public static final Pattern ZIP_PATTERN_ch_CH = Pattern.compile("[0-9]{4}");

    public static final Pattern ZIP_PATTERN_lv = Pattern.compile("[0-9]{4}");

    public static final Pattern ZIP_PATTERN_ee = Pattern.compile("[0-9]{5}");

    // see
    // http://stackoverflow.com/questions/164979/uk-postcode-regex-comprehensive
    public static final Pattern ZIP_PATTERN_gb = Pattern.compile(
            "(GIR 0AA)|((([A-Z][0-9][0-9]?)|(([A-Z][A-Z][0-9][0-9]?)|(([A-Z][0-9][A-Z])|([A-Z][A-Z][0-9][ABEHMNPRVWXY]))))\\s?[0-9][A-Z]{2})");

    public static final String transformInputToValidZipForNl(String input) {
        if (!ZIP_PATTERN_nl_NL_Register.matcher(input).matches()) {
            throw new IllegalArgumentException();
        } else if (ZIP_PATTERN_nl_NL.matcher(input).matches()) {
            return input;
        } else {
            return input.substring(0, 4) + " " + input.substring(4);
        }
    }

    public static final String transformInputToValidZipForCz(String input) {
        if (!ZIP_PATTERN_cz_CZ_Register.matcher(input).matches()) {
            throw new IllegalArgumentException();
        } else if (ZIP_PATTERN_cz_CZ.matcher(input).matches()) {
            return input;
        } else {
            return input.substring(0, 3) + " " + input.substring(3);
        }
    }

    public final Locale locale;

    public final Currency currency;

    public final String currencyCodeHtml;

    public final KnownCurrencies knownCurrency;

    LocaleInfosBean(Locale locale, KnownCurrencies knownCurrency) {
        this.locale = requireNonNull(locale);
        this.knownCurrency = knownCurrency;
        this.currency = knownCurrency.currency;
        this.currencyCodeHtml = knownCurrency.currencyHtml;
    }

    public static void main(String[] args) {
        System.out.println(ZIP_PATTERN_cz_CZ.matcher("223 33").matches());
    }
}
