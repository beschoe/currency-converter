package com.mercateo.common.i18n;

import java.util.HashSet;

import com.mercateo.common.util.CollectionUtil;
import com.mercateo.common.util.LogMessageCommonTags;
import com.mercateo.common.util.ThreadLogger;

public class MissingLocalizationsMgr {
    /**
     * keys with this prefix are NOT reported
     */
    public static final String KEY_PREFIX_DEV_UNDERSCORE = "DEV_";

    private MissingLocalizationsMgr() {
        // static only class;
    }

    // NOTE: using statics with stateful objects is in general a bad idea. we do
    // it here, because it is only for diagnostic purposes.
    private static final HashSet<String> MISSING_KEYS = new HashSet<String>();

    public static int getMissingLocalizationCount() {
        synchronized (MISSING_KEYS) {
            return MISSING_KEYS.size();
        }
    }

    public static String[] getMissingLocalizations() {
        final String[] result;
        synchronized (MISSING_KEYS) {
            result = CollectionUtil.toArray(MISSING_KEYS, String.class);
        }
        return result;
    }

    private static boolean needLogging(Language language, String key) {
        final boolean needLogging;
        final String loggingKey = language.isoLanguage2 + "!" + key;
        synchronized (MISSING_KEYS) {
            needLogging = MISSING_KEYS.add(loggingKey);
        }
        return needLogging;
    }

    public static void reportMissingKey(Language language, String key) {
        if (!key.startsWith(KEY_PREFIX_DEV_UNDERSCORE)) {
            if (needLogging(language, key)) {
                reportMissingKeyNoCheck(language.isoLanguage2, key);
            }
        }
    }

    public static void reportMissingKeyNoCheck(String language, String key) {
        ThreadLogger.logTagged("Missing key \"" + key + "\" for language " + language,
                LogMessageCommonTags.LOGGING_TAG_Translations);
    }

}