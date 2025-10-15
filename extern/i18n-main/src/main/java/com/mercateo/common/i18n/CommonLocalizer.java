package com.mercateo.common.i18n;

import static java.util.Objects.requireNonNull;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.mercateo.common.util.annotations.NonNullByDefault;
import com.mercateo.common.util.annotations.Nullable;
import com.mercateo.common.util.annotations.VisibleForTesting;
import com.mercateo.common.util.io.inputcache.ILoader;
import com.mercateo.common.util.string.EscapeUtils;

/*
 * TODO: All constructors of this class are either @VisibleForTesting or @Deprecated.
 * How can/should this class be used/subclassed?
 */

@NonNullByDefault
public class CommonLocalizer extends CommonMercateoLocalizer {

    // TODO: The name is misleading. It is not an identity function but a
    // constant function that always returns null.
    @SuppressWarnings("rawtypes")
    private final static ILoader IDENTITY = new ILoader() {

        @Override
        @Nullable
        public Object load(@Nullable Object key) {
            // TODO Auto-generated method stub
            return null;
        }
    };

    public static final Pattern CONTAIN_ARGS_PATTERN = Pattern.compile("\\{\\d.*}");

    @Nullable
    private final ResourceBundle resourceBundle;

    @Nullable
    private final ResourceBundle defaultResourceBundle;

    private final Map<String, MessageFormat> messageFormatCache = new HashMap<>();

    private final Language language;

    private final @Nullable GeographicalArea geographicalArea;

    private CommonLocalizer(Locale locale, @Nullable KnownCurrencies currency,
            @Nullable ILoader<String, String> localizedUnitsMapping,
            @Nullable final ResourceBundle resourceBundle,
            @Nullable final ResourceBundle defaultResourceBundle, final Language language,
            boolean roundHUF) {
        super(requireNonNull(locale), currency, localizedUnitsMapping, roundHUF);
        this.resourceBundle = resourceBundle;
        this.defaultResourceBundle = defaultResourceBundle;
        this.language = requireNonNull(language);
        this.geographicalArea = null;
    }

    @VisibleForTesting
    protected CommonLocalizer(Locale locale, GeographicalArea geographicalArea,
            @Nullable ILoader<String, String> localizedUnitsMapping,
            @Nullable final ResourceBundle resourceBundle,
            @Nullable final ResourceBundle defaultResourceBundle, final Language language,
            DecimalFormatterFactory decimalFormatterFactory) {

        super(requireNonNull(locale), localizedUnitsMapping,
                decimalFormatterFactory);
        this.resourceBundle = resourceBundle;
        this.defaultResourceBundle = defaultResourceBundle;
        this.language = requireNonNull(language);
        this.geographicalArea = geographicalArea;
    }

    @VisibleForTesting
    protected CommonLocalizer(Locale locale, GeographicalArea geographicalArea,
            @Nullable KnownCurrencies currency, @Nullable ILoader<String, String> localizedUnitsMapping,
            @Nullable final ResourceBundle resourceBundle,
            @Nullable final ResourceBundle defaultResourceBundle, final Language language,
            boolean roundHUF) {

        super(requireNonNull(locale), currency, localizedUnitsMapping, roundHUF);
        this.resourceBundle = resourceBundle;
        this.defaultResourceBundle = defaultResourceBundle;
        this.language = requireNonNull(language);
        this.geographicalArea = geographicalArea;
    }

    /**
     * @deprecated We need this "poor man's static factory" because of the class
     *             hierarchy. If possible, avoid it and use the other
     *             straightforward constructor. Additionally, you could simplify
     *             the code at the caller site with the extra knowledge whether
     *             baseName is null.
     *
     * @param locale
     * @param currency
     * @param localizedUnitsMapping
     * @param baseName
     * @deprecated
     */
    @Deprecated
    protected CommonLocalizer(Locale locale, @Nullable KnownCurrencies currency,
            @Nullable ILoader<String, String> localizedUnitsMapping, @Nullable String baseName) {
        this(requireNonNull(locale), currency, localizedUnitsMapping,
                /* resource bundle: */
                baseName != null && baseName.trim().length() > 0 ? createResourceBundle(locale,
                        baseName) : null,

                /* default resource bundle: */
                baseName != null && baseName.trim().length() > 0 ? createDefaultResourceBundle(
                        baseName) : null,

                Language.forISOWithGermanDefault(locale.getLanguage()), false);
    }

    protected @Nullable GeographicalArea getGeographicalArea() {
        return geographicalArea;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> ILoader<K, V> identityLoader() {
        return IDENTITY;

    }

    public static ResourceBundle createDefaultResourceBundle(String baseName) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return ResourceBundle.getBundle(baseName, DEFAULT_LOCALE, classLoader);
    }

    @Nullable
    public static ResourceBundle createResourceBundle(Locale locale, String baseName) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            return ResourceBundle.getBundle(baseName, locale, classLoader);
        } catch (MissingResourceException e) {
            try {
                return createDefaultResourceBundle(baseName);
            } catch (MissingResourceException e1) {
                logger.error("Could not get resource bundle for locale \"" + locale
                        + "\" nor for the default locale");
                return null;
            }
        }
    }

    public static CommonLocalizer getInstance(
            @Nullable ILoader<String, String> localizedUnitsMapping, @Nullable Locale locale,
            String resourceBundlebaseName) {
        return createCommonLocalizerWithoutCurrency(locale,
                localizedUnitsMapping, resourceBundlebaseName);
    }

    /**
     * @param locale
     *            (may be null. if null, default(=DE) is used).
     * @param resourceBundleBaseName
     *            required.
     * @return CommonLocalizer
     */
    public static CommonLocalizer getInstance(@Nullable Locale locale,
            String resourceBundleBaseName) {
        return createCommonLocalizerWithoutCurrency(locale != null ? locale
                : CommonMercateoLocalizer.DEFAULT_LOCALE, null, resourceBundleBaseName);
    }

    private static CommonLocalizer createCommonLocalizerWithoutCurrency(Locale locale,
            @Nullable ILoader<String, String> localizedUnitsMapping,
            @Nullable String resourceBundleBaseName) {
        requireNonNull(locale);

        final ResourceBundle resourceBundle;
        final ResourceBundle defaultResourceBundle;
        if (resourceBundleBaseName != null && resourceBundleBaseName.trim().length() > 0) {
            resourceBundle = createResourceBundle(locale, resourceBundleBaseName);
            defaultResourceBundle = createDefaultResourceBundle(resourceBundleBaseName);
        } else {
            resourceBundle = null;
            defaultResourceBundle = null;
            resourceBundleBaseName = "null";
        }

        final Language language = Language.forISOWithGermanDefault(locale.getLanguage());
        final KnownCurrencies missingCurrency_null = null;

        return new CommonLocalizer(locale, missingCurrency_null, localizedUnitsMapping,
                resourceBundle, defaultResourceBundle, language, false);
    }

    public static Object[] escapeArguments(Object... arguments) {
        Object[] escapedArguments = arguments.clone();
        for (int i = 0; i < escapedArguments.length; i++) {
            Object object = escapedArguments[i];
            if (object instanceof String) {
                escapedArguments[i] = EscapeUtils.escape((String) object, false);
            }
        }
        return escapedArguments;
    }

    @Override
    public boolean hasString(String key) {
        final ResourceBundle resourceBundle = this.resourceBundle;
        if (resourceBundle == null) {
            return true;
        }

        return resourceBundle.containsKey(key);
    }

    /**
     * Returns a localized string based on the given <code>key</code>. The
     * localized string is looked up in property resource files.
     * <p>
     * If problems occur, an appropriate non-null default string is returned.
     *
     * @param key
     * @return the localized string or a non-null default string
     */
    @Override
    public String getString(String key) {
        return getStringFromResourceBundle(key);
    }

    /**
     * Same as {@link #getString(String)}, but formats the localized string with
     * a <code>MessageFormat</code>(only applied for Strings with Parameters).
     *
     * @param key
     * @param arguments
     * @return //
     */
    @Override
    public String getString(String key, Object... arguments) {
        MessageFormat messageFormat;
        synchronized (this.messageFormatCache) {
            messageFormat = this.messageFormatCache.get(key);
            if (messageFormat == null) {
                String stringFromResourceBundle = this.getStringFromResourceBundle(key);
                if (containsArgs(stringFromResourceBundle)) {
                    messageFormat = new MessageFormat(stringFromResourceBundle, this.locale);
                    this.messageFormatCache.put(key, messageFormat);
                } else {
                    return stringFromResourceBundle;
                }
            }
        }

        synchronized (messageFormat) {
            return messageFormat.format(arguments, new StringBuffer(), null).toString();
        }
    }

    private boolean containsArgs(String string) {
        return CONTAIN_ARGS_PATTERN.matcher(string).find();
    }

    private String getStringFromResourceBundle(final String key) {
        ResourceBundle resourceBundle = this.resourceBundle;
        if (resourceBundle == null) {
            return key;
        }

        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException ex) {
            MissingLocalizationsMgr.reportMissingKey(this.getLanguage(), key);

            logger.warn("Resource \"" + key + "\" missing from " + resourceBundle
                    .getBaseBundleName() + ", locale: " + super.locale);
            ResourceBundle defaultResourceBundle = this.defaultResourceBundle;
            if (defaultResourceBundle == null) {
                return key;
            }
            try {
                return defaultResourceBundle.getString(key);
            } catch (MissingResourceException ex2) {
                logger.warn("Resource \"" + key + "\" missing from " + resourceBundle
                        .getBaseBundleName() + ", locale: " + defaultResourceBundle.getLocale());
                return key;
            }

        }
    }

    /**
     * Shorthand method for printing localized strings in JSPs. National letters
     * are escaped, but SGML meta characters (less, ampersand, ...) are not.
     *
     * @param key
     * @return the JSP safe localized string
     */
    @Override
    public String ls(String key) {
        return EscapeUtils.sgmlEscapeLetters(this.getString(key));
    }

    /**
     * Shorthand method for printing localized strings in JSPs. National letters
     * are escaped, but SGML meta characters (less, ampersand, ...) are not.
     *
     * @param key
     * @param arguments
     *            Substitution arguments, see
     *            <code>java.text.MessageFormat</code>. WARNING: must be escaped
     *            unless they really can contain html tags, e.g. with
     *            {@link #escapeArguments(Object[])}
     * @return the JSP safe localized string
     */
    @Override
    public String ls(String key, Object... arguments) {
        return EscapeUtils.sgmlEscapeLetters(this.getString(key, arguments));
    }

    /**
     * Method for printing localized strings for GeoAreaSpecificMessage in JSPs.
     * National letters are escaped, but SGML meta characters (less, ampersand,
     * ...) are not.
     *
     * @param key
     *            the message key which follows &quot;<strong>
     *            &lt;key&gt;_&lt;GEO_AREA_CODE&gt;</strong> &quot; convention
     * @return the JSP safe localized string
     */
    @Override
    public String getEscapedGeoAreaMsg(String key) {
        return EscapeUtils.sgmlEscapeLetters(this.getGeoAreaSpecificMessage(key));
    }

    /**
     * Method for printing localized strings for GeoAreaSpecificMessage in JSPs.
     * National letters are escaped, but SGML meta characters (less, ampersand,
     * ...) are not.
     *
     * @param key
     *            the message key which follows &quot;<strong>
     *            &lt;key&gt;_&lt;GEO_AREA_CODE&gt;</strong> &quot; convention
     *
     * @param arguments
     *            Substitution arguments, see
     *            <code>java.text.MessageFormat</code>. WARNING: must be escaped
     *            unless they really can contain html tags, e.g. with
     *            {@link #escapeArguments(Object[])}
     * @return the JSP safe localized string
     */
    @Override
    public String getEscapedGeoAreaMsg(String key, Object... arguments) {
        return EscapeUtils.sgmlEscapeLetters(this.getGeoAreaSpecificMessage(key, arguments));
    }

    /**
     * Shorthand method for printing localized strings in JSPs. Special
     * characters are not escaped and are returned "as they are". (The "r" in
     * the method name stands for "raw".)
     *
     * @param key
     * @return the JSP safe localized "raw" string
     */
    @Override
    public String rls(String key) {
        return this.getString(key);
    }

    /**
     * Shorthand method for printing localized strings in JSPs. Special
     * characters are not escaped and are returned "as they are". (The "r" in
     * the method name stands for "raw".)
     *
     * @param key
     * @param arguments
     *            Substitution arguments, see
     *            <code>java.text.MessageFormat</code>.
     * @return the JSP safe localized "raw" string
     */
    @Override
    public String rls(String key, Object... arguments) {
        return this.getString(key, arguments);
    }

    public Language getLanguage() {
        return language;
    }

    @Override
    public String getGeoAreaSpecificMessage(String key) {
        assertGeoAreaIsPresent();
        String regionSpecificKey = key + getAreaCodeSuffix();
        return this.getString(regionSpecificKey);
    }

    @Override
    public String getGeoAreaSpecificMessage(String key, Object... args) {
        assertGeoAreaIsPresent();
        String regionSpecificKey = key + getAreaCodeSuffix();
        return this.getString(regionSpecificKey, args);
    }

    private String getAreaCodeSuffix() {
        GeographicalArea geographicalArea2 = this.geographicalArea;
        assert geographicalArea2 != null;
        return "_" + geographicalArea2.name().toUpperCase();
    }

    private void assertGeoAreaIsPresent() {
        if (this.geographicalArea == null) {
            throw new IllegalStateException("The geographical area is null");
        }
    }
}