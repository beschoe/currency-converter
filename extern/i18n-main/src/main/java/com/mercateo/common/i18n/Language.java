package com.mercateo.common.i18n;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.mercateo.common.util.ArrayUtil;
import com.mercateo.common.util.annotations.NonNull;
import com.mercateo.common.util.annotations.NonNullByDefault;
import com.mercateo.common.util.annotations.Nullable;

/**
 * This enum represents Mercateo-supported language codes according to ISO-639
 *
 * All codes are lower-case.
 *
 * @author rene.mazala
 */
@NonNullByDefault
public enum Language {
    GERMAN("de", "deu", "ger"),

    DUTCH("nl", "nld", "dut"),

    ENGLISH("en", "eng"),

    ITALIAN("it", "ita"),

    SPANISH("es", "spa"),

    CZECH("cs", "ces", "cze"),

    POLISH("pl", "pol"),

    SLOVAK("sk", "slk", "slo"),

    HUNGARIAN("hu", "hun"),

    FRENCH("fr", "fra", "fre"),

    /*
     * TODO ITHD-6020, PI-235: Why don't support swedish?
     */
    // SWEDISH("sv", "swe", "swe"),
    ;

    /**
     * the iso 2 letter code
     */
    public final String isoLanguage2;

    /**
     * the iso 3 letter code (if B and T exist, this is the T variation)
     */
    public final String isoLanguage3;

    /**
     * the iso 3 letter code B variation (if B and T exist) or null
     */
    @Nullable
    public final String isoLanguage3B;

    Language(String isoLanguage2, String isoLanguage3) {
        this.isoLanguage2 = isoLanguage2;
        this.isoLanguage3 = isoLanguage3;
        this.isoLanguage3B = null;
    }

    Language(String isoLanguage2, String isoLanguage3T, String isoLanguage3B) {
        this.isoLanguage2 = isoLanguage2;
        this.isoLanguage3 = isoLanguage3T;
        this.isoLanguage3B = isoLanguage3B;
    }

    public String getNameInNativeLanguage() {
        final Locale locale = new Locale(this.isoLanguage2);
        return locale.getDisplayLanguage(locale);
    }

    @Nullable
    public static final Language NULL = null;

    private static Map<String, Language> ISO_TO_LANGUAGE_MAP = Collections.unmodifiableMap(
            new HashMap<String, Language>(Language.values().length) {
                {
                    for (Language language : Language.values()) {
                        put(language.isoLanguage2, language);
                    }
                }
            });

    /**
     * default language for migration code. use this constant to mark places
     * that have to be revisited for other countries.
     *
     */
    public static Language DEFAULT_GERMAN = GERMAN;

    /**
     * the mkx language is german. use this constant for place that refer to
     * this fact
     *
     */
    public static Language MKX_GERMAN = GERMAN;

    public static Language forISOWithGermanDefault(String languageString) {
        final Language language = ISO_TO_LANGUAGE_MAP.get(languageString);
        if (language != null) {
            return language;
        }
        return GERMAN;
    }

    @Nullable
    public static Language forISO(String languageString) {
        return ISO_TO_LANGUAGE_MAP.get(languageString);
    }

    /**
     * turn string array into enum array, ignoring unknown values.
     *
     * @param strings
     *            (elements may be null)
     * @return T[]
     */
    public static Language[] getArrayForISO(String[] strings) {
        Language[] result = new Language[strings.length];
        int j = 0;
        for (String s : strings) {
            if (s != null) {
                Language language = forISO(s);
                if (language != null) {
                    result[j++] = language;
                }
            }
        }
        return j == result.length ? result : ArrayUtil.subarray(result, 0, j);
    }

    @SuppressWarnings("null")
    public static Optional<Language> of(@NonNull Locale locale) {
        return Optional.ofNullable(Language.forISO(locale.getLanguage()));
    }
}
