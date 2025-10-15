/*
 * Created on 19.02.2009
 *
 * author felix
 */
package com.mercateo.common.i18n;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;

import com.mercateo.common.i18n.units.LocalizedUnitKey;
import com.mercateo.common.i18n.units.LocalizedUnitValue;
import com.mercateo.common.util.annotations.NonNullByDefault;
import com.mercateo.common.util.annotations.Nullable;
import com.mercateo.common.util.io.inputcache.ILoader;
import com.mercateo.common.util.math.BigDecimalUtil;

@NonNullByDefault
public abstract class CommonMercateoLocalizer implements MercateoLocalizer {

    protected static final Logger logger = org.slf4j.LoggerFactory.getLogger(
            CommonMercateoLocalizer.class.getName());

    @Nullable
    protected final ILoader<String, String> localizedUnitsMapping;

    protected final Locale locale;

    public final static Locale DEFAULT_LOCALE = GeographicalArea.DEFAULT_de.getLocale();

    private final Locale[] localesForBDParsing;

    public final static String PRICE_FORMAT = "###,###,##0.00;-###,###,##0.00";

    private final static String PRICE_FORMAT_HUF = "###,###,##0.##;-###,###,##0.##";

    private final static String QUANTITY_FORMAT = "###,###,##0.#####";

    private final DateFormat dateFormat;

    private final DateFormat timeFormat;

    private final DecimalFormat oneDecimalDigitFormatter;

    private final NumberFormat numberFormatter;

    private final DecimalFormatterFactory decimalFormatterFactory;

    protected CommonMercateoLocalizer(Locale locale, @Nullable KnownCurrencies currency,
            @Nullable ILoader<String, String> localizedUnitsMapping,
            boolean roundHUF) {
        this(locale, localizedUnitsMapping,
                new DecimalFormatterFactory(PRICE_FORMAT, PRICE_FORMAT_HUF, QUANTITY_FORMAT, locale, currency, roundHUF));
    }

    protected CommonMercateoLocalizer(Locale locale,
            @Nullable ILoader<String, String> localizedUnitsMapping,
            DecimalFormatterFactory decimalFormatterFactory) {

        this.locale = requireNonNull(locale);
        this.localizedUnitsMapping = localizedUnitsMapping;
        if (this.locale.equals(Locale.ENGLISH)) {
            this.localesForBDParsing = new Locale[] { this.locale };
        } else {
            this.localesForBDParsing = new Locale[] { this.locale, Locale.ENGLISH };
        }
        this.oneDecimalDigitFormatter = new DecimalFormat("0.#", new DecimalFormatSymbols(
                this.locale));
        this.numberFormatter = NumberFormat.getInstance(this.locale);
        this.decimalFormatterFactory = decimalFormatterFactory;

        this.dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, this.locale);
        this.timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, this.locale);

    }

    @Override
    public DecimalFormat getPriceFormatter() {
        return this.decimalFormatterFactory.getPriceFormatter();
    }

    @Override
    public DecimalFormat getQuantityFormatter() {
        return this.decimalFormatterFactory.getQuantityFormatter();
    }

    /**
     * Note: this will throw an localizedUnitsMapping, if localizedUnitsMapping was
     * initialized to null
     * 
     * @throws IllegalStateException
     *             if this.localizedUnitsMapping is null.
     */
    @Override
    public String lu(String unit, boolean printPreview, boolean isSingular)
            throws IllegalStateException {
        final ILoader<String, String> localizedUnitsMapping2 = this.localizedUnitsMapping;
        if (localizedUnitsMapping2 == null) {
            throw new IllegalStateException("localizedUnitsMapping is null");
        }
        final String key = new LocalizedUnitKey(unit, this.locale.getLanguage()).encode();
        String localizedUnitValueString = localizedUnitsMapping2.load(key);
        if (localizedUnitValueString == null) {
            // try the german locale as fallback
            final String germanKey = new LocalizedUnitKey(unit,
                    Language.GERMAN.isoLanguage2).encode();
            localizedUnitValueString = localizedUnitsMapping2.load(germanKey);
            if (localizedUnitValueString == null) {
                // still null -> return the unit
                logger.trace("Missing Unit-Localization for " + unit + " in " + this.locale
                        .toString());
                return unit;
            }
        }
        LocalizedUnitValue localizedUnitValue = LocalizedUnitValue.decode(localizedUnitValueString);
        return isSingular ? localizedUnitValue.getSingular() : localizedUnitValue.getPlural();
    }

    public Locale[] getLocalesForBDParsing() {
        return this.localesForBDParsing;
    }

    /**
     * Note: this will crash, if localizedUnitsMapping was initialized to null
     * 
     * @param unit
     * @param quantity
     * @return String
     */
    public String lu(String unit, BigDecimal quantity) {
        return lu(unit, false, BigDecimalUtil.isSingular(quantity));
    }

    public String formatToOneDigit(BigDecimal bigDecimal) {
        final String result;
        synchronized (this.oneDecimalDigitFormatter) {
            result = this.oneDecimalDigitFormatter.format(bigDecimal);
        }
        return result;
    }

    public String formatNumber(BigDecimal bigDecimal) {
        final String result;
        synchronized (this.numberFormatter) {
            result = this.numberFormatter.format(bigDecimal);
        }
        return result;

    }

    public String getLocaleLanguage() {
        return this.locale.getLanguage();
    }

    /**
     * Shorthand method for formatting a quantity.
     */
    @Override
    public String lq(double value) {
        return this.getQuantityFormatter().format(value);
    }

    /**
     * Shorthand method for formatting a quantity.
     */
    @Override
    public String lq(Number value) {
        return this.getQuantityFormatter().format(value.doubleValue());
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

    public String getFormattedDate(Date date) {
        return this.dateFormat.format(date);
    }

    public String getFormattedPriceWithoutCurrency(@Nullable BigDecimal price) {
        return price != null ? this.getPriceFormatter().format(price) : "";
    }

    public String getFormattedTime(Date date) {
        return this.timeFormat.format(date);
    }

    public String getFormattedDate(Calendar calendar) {
        return this.getFormattedDate(calendar.getTime());
    }

    public String removeHeadingZeros(String value) {
        return value.replaceFirst("0*", "");
    }

    @Override
    public String formatAsPriceAmount(@Nullable BigDecimal bigDecimal) {
        // bigDecimal==null should only happen in admin mode or for content
        // articles
        return bigDecimal != null ? this.getPriceFormatter().format(
                bigDecimal) : "-";
    }
    
    public String formatAsFullScalePriceAmount(BigDecimal bigDecimal) {
        return this.decimalFormatterFactory.getPriceFormatterForScale(bigDecimal.scale()).format(
                bigDecimal);
    }

}