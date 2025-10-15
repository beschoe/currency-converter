/*
 * Created on 10.10.2008
 *
 * author sandra
 */
package com.mercateo.common.i18n;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mercateo.common.util.EnumUtils;
import com.mercateo.common.util.annotations.NonNullByDefault;
import com.mercateo.common.util.annotations.Nullable;

/**
 * An enum for supported countries. The enum name MUST BE generated as
 * two-letter ISO-3166 country code converted to lower-case.
 */
@NonNullByDefault
public enum GeographicalArea {

    /*
     * Germany. We use ".com" domain here.
     */

    de(new Language[] { Language.GERMAN }, "com", KnownCurrencies.EUR),

    /*
     * Austria
     */
    at(new Language[] { Language.GERMAN }, "at", KnownCurrencies.EUR),

    /*
     * Netherlands
     */
    nl(new Language[] { Language.DUTCH }, "nl", KnownCurrencies.EUR),

    /*
     * maybe add GERMAN Italy
     */
    it(new Language[] { Language.ITALIAN }, "it", KnownCurrencies.EUR),

    /*
     * France
     */
    fr(new Language[] { Language.FRENCH }, "fr", KnownCurrencies.EUR),

    /*
     * Spain
     */
    es(new Language[] { Language.SPANISH }, "es", KnownCurrencies.EUR),

    /*
     * Great Britain
     */
    gb(new Language[] { Language.ENGLISH }, "co.uk", KnownCurrencies.GBP),

    /*
     * Ireland
     */
    ie(new Language[] { Language.ENGLISH }, "ie", KnownCurrencies.EUR),

    /*
     * Czechia
     */
    cz(new Language[] { Language.CZECH }, "com", KnownCurrencies.CZK),

    /*
     * Poland
     */
    pl(new Language[] { Language.POLISH }, "com.pl", KnownCurrencies.PLN),

    /*
     * Slovakia
     *
     * ATTENTION: Currency.getInstance(new Locale("sk", "SK")).getCurrencyCode()
     * returns "SKK", which is wrong since 2009-01-01, when Slovakia joined the
     * EUR-zone.
     */
    sk(new Language[] { Language.SLOVAK }, "com", KnownCurrencies.EUR),

    /*
     * Hungary
     */
    hu(new Language[] { Language.HUNGARIAN }, "hu", KnownCurrencies.HUF),

    /**
     * Switzerland German>French>Italian
     */
    ch(new Language[] { Language.GERMAN, Language.FRENCH, Language.ITALIAN }, "ch",
            KnownCurrencies.CHF),

    /*
     * Belgium. Dutch>French>German
     *
     * ! Note: most software components will only work with the first language
     */
    be(new Language[] { Language.DUTCH, Language.FRENCH, Language.GERMAN }, "be",
            KnownCurrencies.EUR),

    /*
     * Luxembourg Luxembourgian>French>German
     *
     * http://lb.wikipedia.org/wiki/L%C3%ABtzebuerg_(Land): Letzebuergesch, Daitsch,
     * Franseisch
     *
     * TODO add Language for Letzeburgisch liste der sprachen Sprachen laut
     */
    lu(new Language[] { Language.GERMAN, Language.FRENCH }, "com", KnownCurrencies.EUR),

    /*
     * Schweden
     *
     * TODO ITHD-6020, PI-235: Why don't support and use Language.SWEDISH?
     */
    se(new Language[] { Language.ENGLISH }, "com", KnownCurrencies.SEK),

    /*
     * China
     *
     * default language: English
     *
     */
    cn(new Language[] { Language.ENGLISH }, "com", KnownCurrencies.CNY),

    /*
     * Indien
     *
     * default language: English
     *
     */
    in(new Language[] { Language.ENGLISH }, "com", KnownCurrencies.INR),

    /*
     * Brasilien
     *
     * default language: English
     *
     */
    br(new Language[] { Language.ENGLISH }, "com", KnownCurrencies.BRL),

    /*
     * Denmark
     *
     * default language: English
     *
     */
    dk(new Language[] { Language.ENGLISH }, "com", KnownCurrencies.DKK),

    /*
     * Mexiko
     *
     * default language: Spanish
     *
     */
    mx(new Language[] { Language.SPANISH }, "com", KnownCurrencies.MXN),

    /*
     * USA
     *
     * default language: English
     *
     */
    us(new Language[] { Language.ENGLISH }, "com", KnownCurrencies.USD),

    /*
     * Latvia
     *
     * default language: English
     *
     */
    lv(new Language[] { Language.ENGLISH }, "com", KnownCurrencies.EUR),

    /*
     * Estonia
     *
     * default language: English
     *
     */
    ee(new Language[] { Language.ENGLISH }, "com", KnownCurrencies.EUR);

    private static final String IPV4_REGEX = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";

    private static final Pattern MERCATEO_HOST_WITH_SUFFIX = Pattern.compile(".*mercateo.(.*)");

    private static final Pattern MERCATEO_HOST_WITH_PREFIX = Pattern.compile(
            "(^(?!www)(.*)).mercateo.*");

    private final LocaleInfosBean localeInfosBean;

    /**
     * e.g. "at" for austria, "com" for germany or "co.uk" for united kingdom
     *
     * TODO Think about domain suffixes as array, e.g. for Germany use "de" and
     * "com"
     */
    public final String domainSuffix;

    /**
     * The ISO-639 two letter code as enum.
     *
     * Note: This represents only the language code, not the combination with
     * country and/or variant. E.g.: "en" for Ireland and not "en_IE".
     *
     * @xdeprecated this doesnt work in countries like switzerland and belgium.
     *              contains first element of languages. use {@link #languages}
     *              instead or, in most places, the language setting that is derived
     *              from displaysettings AND viewname should be used (like in the
     *              localizer) should be used.
     */
    public final Language language;

    public final Language[] languages;

    /**
     * The 2-letter upper case country code according to ISO-3166-1 ALPHA-2
     *
     * @see "http://de.wikipedia.org/wiki/ISO-3166-1-Kodierliste"
     */
    public final String iso3166alpha2UpperCaseCode;

    GeographicalArea(Language[] languages, String domainSuffix, KnownCurrencies knownCurrency) {
        this.language = languages[0];
        this.languages = languages;
        this.iso3166alpha2UpperCaseCode = this.name().toUpperCase();
        final Locale locale = createLocale(this.language.isoLanguage2);
        assert locale.getLanguage().equals(this.language.isoLanguage2);
        this.localeInfosBean = new LocaleInfosBean(locale, knownCurrency);
        this.domainSuffix = domainSuffix;
    }

    public Locale createLocale(String languageForLocale) {
        return new Locale(languageForLocale, this.iso3166alpha2UpperCaseCode);
    }

    public LocaleInfosBean getLocaleInfosBean() {
        return this.localeInfosBean;
    }

    public Locale getLocale() {
        return this.localeInfosBean.locale;
    }

    public String getNameInDefaultLanguage() {
        Locale locale = this.getLocale();
        return locale.getDisplayCountry(locale);
    }

    public String getNameInLanguage(Language language) {
        Locale geographicalAreaLocale = this.getLocale();
        Locale languageLocale = new Locale(language.isoLanguage2);
        return geographicalAreaLocale.getDisplayCountry(languageLocale);
    }

    private static final String[] AVAILABLE_LANGUAGES;

    static {
        final Set<String> langs = new HashSet<>();
        for (GeographicalArea area : GeographicalArea.values()) {
            langs.add(area.language.isoLanguage2);
        }
        AVAILABLE_LANGUAGES = langs.toArray(new String[0]);
        Arrays.sort(AVAILABLE_LANGUAGES);
    }

    /**
     * TODO: maybe move this to Language.java
     *
     * @return String[] all available ISO language codes as sorted array
     */
    public static String[] getAvailableLanguages() {
        return AVAILABLE_LANGUAGES;
    }

    /**
     * Default geographical area is Germany "de"
     */
    public static final GeographicalArea DEFAULT_de = GeographicalArea.de;

    /**
     * Checks whether the string representation of a geographical area is valid.
     *
     * @param geographicalAreaAsString
     * @return <code>true</code> if parameter country is a known geographical area
     */
    public static boolean isValidGeographicalArea(String geographicalAreaAsString) {
        final GeographicalArea geographicalArea = EnumUtils.getSafeEnumValueOf(
                geographicalAreaAsString, GeographicalArea.class);
        return geographicalArea != null;
    }

    /**
     * Gets the geographical area object corresponding to a string. If the string
     * parameter is not a valid representation of a <code>GeographicalArea</code>
     * then the default geographical area is returned.
     *
     * @param geographicalArea
     * @return the corresponding geographical area object, or the default
     *         geographical area if the string parameter represents an invalid
     *         geographical area
     * @see GeographicalArea#DEFAULT_de
     */
    public static GeographicalArea getGeographicalAreaOrDefault(@Nullable String geographicalArea) {
        return EnumUtils.getSafeEnumValueOf(geographicalArea, GeographicalArea.class,
                GeographicalArea.DEFAULT_de);
    }

    /**
     * Gets the geographical area object corresponding to a string. If the string
     * parameter is not a valid representation of a <code>GeographicalArea</code>
     * then <code>null</code> is returned.
     *
     * @param geographicalArea
     * @return the corresponding geographical area object, or the <code>null</code>
     *         geographical area if the string parameter represents an invalid
     *         geographical area
     */
    @Nullable
    public static GeographicalArea getGeographicalArea(String geographicalArea) {
        return EnumUtils.getSafeEnumValueOf(geographicalArea, GeographicalArea.class);
    }

    public static GeographicalArea @Nullable [] getGeographicalAreas(
            String @Nullable [] countries) {
        if (countries == null) {
            return null;
        }

        final EnumSet<GeographicalArea> geographicalAreas = EnumSet.noneOf(GeographicalArea.class);
        for (String country : countries) {
            final GeographicalArea ga = getGeographicalAreaOrDefault(country);
            if (!geographicalAreas.contains(ga)) {
                geographicalAreas.add(ga);
            }
        }
        return geographicalAreas.toArray(new GeographicalArea[0]);
    }

    /**
     * Extracts the {@link GeographicalArea} from a host name. The host name can
     * have the form
     * <ul>
     * <li><code>at.mercateo.com</code> (country is first part of host name)
     * <li><code>www.mercateo.at</code> (country is last part of host name)
     * </ul>
     * If no valid country can be extracted then the default geographical area is
     * returned. See {@link GeographicalArea#DEFAULT_de}
     *
     * @param serverName
     * @return {@link GeographicalArea}
     *
     */
    public static GeographicalArea extractGeographicalAreaFromServerName(
            @Nullable String serverName) {
        if (serverName == null) {
            return GeographicalArea.DEFAULT_de;
        }
        if (serverName.matches(IPV4_REGEX)) {
            return GeographicalArea.DEFAULT_de;
        }

        Matcher prefixMatcher = MERCATEO_HOST_WITH_PREFIX.matcher(serverName);
        if (prefixMatcher.find()) {
            String potentialCountryCode = prefixMatcher.group(1);
            GeographicalArea geographicalArea = EnumUtils.getSafeEnumValueOf(potentialCountryCode,
                    GeographicalArea.class);
            if (geographicalArea != null) {
                return geographicalArea;
            }
        }

        Matcher suffixMatcher = MERCATEO_HOST_WITH_SUFFIX.matcher(serverName);
        if (suffixMatcher.find()) {
            return getGeographicalAreaByDomainSuffix(suffixMatcher.group(1));
        } else {
            return GeographicalArea.DEFAULT_de;
        }
    }

    private static GeographicalArea getGeographicalAreaByDomainSuffix(String suffix) {
        GeographicalArea[] values = values();
        for (GeographicalArea geographicalArea : values) {
            if (geographicalArea.domainSuffix.equals(suffix)) {
                return geographicalArea;
            }
        }
        /*
         * fallback strategy: use old way to get GeographicalArea
         */
        return getGeographicalAreaOrDefault(suffix);
    }

    public static GeographicalArea[] supportedGeographicalAreas() {
        return Arrays.stream(GeographicalArea.values()).filter(GeographicalArea::isActive).toArray(
                GeographicalArea[]::new);
    }

    /**
     * Specifies that this GeographicalArea is 'live'.
     * <p>
     * The use of inactive geoAreas is not recommended. They will often be replaced
     * by DEFAULT_de.
     *
     * @return
     */
    public boolean isActive() {
        switch (this) {
        case de:
            return true;
        case at:
            return true;
        case nl:
            return true;
        case it:
            return true;
        case fr:
            return true;
        case es:
            return true;
        case gb:
            return true;
        case ie:
            return true;
        case pl:
            return true;
        case hu:
            return true;
        case be:
            return true;
        case ch:
            return true;
        case sk:
        	return false;
        case cz:
        	return false;
        case lu:
            return false;
        case se:
            return false;
        case cn:
            return false;
        case in:
            return false;
        case br:
            return false;
        case mx:
            return false;
        case dk:
            return false;
        case lv:
            return false;
        case ee:
            return false;
        case us:
            return false;
        }
        assert false : "unhandled Geographical area " + this.name();
        return false;
    }

    private static final Map<String, GeographicalArea> COUNTRY_TO_GA_MAP;

    static {

        final Map<String, GeographicalArea> countryToGAMap = new HashMap<>();
        for (GeographicalArea geographicalArea : GeographicalArea.values()) {
            countryToGAMap.put(geographicalArea.getLocale().getCountry(), geographicalArea);
        }
        COUNTRY_TO_GA_MAP = Collections.unmodifiableMap(countryToGAMap);
    }

    @Nullable
    public static GeographicalArea getGeographicalAreaByUpperCaseCountryCode(String countryCode) {
        return COUNTRY_TO_GA_MAP.get(countryCode);
    }
}
