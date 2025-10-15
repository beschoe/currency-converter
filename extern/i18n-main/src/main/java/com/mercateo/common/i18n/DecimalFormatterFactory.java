package com.mercateo.common.i18n;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.mercateo.common.util.annotations.Nullable;

public class DecimalFormatterFactory {

    private final DecimalFormat priceFormatter;

    private final DecimalFormat quantityFormatter;

    private final String priceFormatHUF;

    private final String priceFormat;

    private final String quantityFormat;

    public DecimalFormatterFactory(String priceFormat, String priceFormatHUF,
            String quantityFormat, Locale locale, @Nullable KnownCurrencies currency,
            boolean roundHUF) {
        this.priceFormat = priceFormat;
        this.priceFormatHUF = priceFormatHUF;
        this.quantityFormat = quantityFormat;
        this.priceFormatter = initPriceFormatter(locale, currency, roundHUF);
        this.quantityFormatter = initQuantityFormatter(locale);
    }

    public DecimalFormat getPriceFormatter() {
        return priceFormatter;
    }
    
    public DecimalFormat getPriceFormatterForScale(int scale) {
        DecimalFormat decimalFormat = new DecimalFormat(priceFormatter.toPattern(), priceFormatter.getDecimalFormatSymbols());
        decimalFormat.setMaximumFractionDigits(Math.max(scale, decimalFormat.getMaximumFractionDigits()));
        return decimalFormat;
    }

    public DecimalFormat getQuantityFormatter() {
        return quantityFormatter;
    }

    private DecimalFormat initQuantityFormatter(Locale locale) {
        return new DecimalFormat(this.quantityFormat,
                new DecimalFormatSymbols(locale));
    }

    private DecimalFormat initPriceFormatter(Locale locale, KnownCurrencies currency,
            boolean roundHUF) {
        return new DecimalFormat(isUseHUFFormat(currency, roundHUF)
                ? this.priceFormatHUF
                : this.priceFormat, new DecimalFormatSymbols(locale));
    }

    private boolean isUseHUFFormat(KnownCurrencies currency, boolean roundHUF) {
        return roundHUF && currency != null && KnownCurrencies.determineDefaultScaleForCurrency(
                currency.currency) == 0;
    }

}
