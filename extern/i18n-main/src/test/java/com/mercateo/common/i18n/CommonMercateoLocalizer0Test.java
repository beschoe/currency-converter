package com.mercateo.common.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import com.mercateo.common.util.annotations.Nullable;
import com.mercateo.common.util.io.inputcache.ILoader;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

public class CommonMercateoLocalizer0Test {
    @Test
    public void lu_singular_and_plural() {
        // given
        Map<String, String> localizedUnitsMap = new HashMap<>();
        localizedUnitsMap.put("theUnit!en", "unit!units");

        CommonMercateoLocalizer uut = setupLocalizer(Locale.ENGLISH, localizedUnitsMap);

        // then
        assertThat(uut.lu("theUnit", false, true)).isEqualTo("unit");
        assertThat(uut.lu("theUnit", false, false)).isEqualTo("units");
    }

    @Test
    public void lu_empty() {
        // given
        Map<String, String> localizedUnitsMap = new HashMap<>();
        localizedUnitsMap.put("theUnit!en", "");
        localizedUnitsMap.put("theUnit!de", "foo!bar");

        CommonMercateoLocalizer uut = setupLocalizer(Locale.ENGLISH, localizedUnitsMap);

        // then
        assertThat(uut.lu("theUnit", false, true)).isEmpty();
        assertThat(uut.lu("theUnit", false, false)).isEmpty();
    }

    @Test
    public void lu_fallback_to_german() {
        // given
        Map<String, String> localizedUnitsMap = new HashMap<>();
        localizedUnitsMap.put("theUnit!de", "foo!bar");

        CommonMercateoLocalizer uut = setupLocalizer(Locale.ENGLISH, localizedUnitsMap);

        // then
        assertThat(uut.lu("theUnit", false, true)).isEqualTo("foo");
        assertThat(uut.lu("theUnit", false, false)).isEqualTo("bar");
    }

    @Test
    public void lu_fallback_to_key() {
        // given
        Map<String, String> localizedUnitsMap = new HashMap<>();

        CommonMercateoLocalizer uut = setupLocalizer(Locale.ENGLISH, localizedUnitsMap);

        // then
        assertThat(uut.lu("theUnit", false, true)).isEqualTo("theUnit");
        assertThat(uut.lu("theUnit", false, false)).isEqualTo("theUnit");
    }

    private CommonMercateoLocalizer setupLocalizer(Locale locale, Map<String, String> localizedUnitsMap) {
        ILoader<String, String> iLoader = new ILoader<String, String>() {
            @Override
            public @Nullable String load(String key) {
                return localizedUnitsMap.get(key);
            }
        };
        return new CommonMercateoLocalizer(locale, null, iLoader, false) {
            @Override
            public String rls(String key, Object... arguments) {
                return null;
            }

            @Override
            public String rls(String key) {
                return null;
            }

            @Override
            public String ls(String key, Object... arguments) {
                return null;
            }

            @Override
            public String ls(String key) {
                return null;
            }

            @Override
            public boolean hasString(String key) {
                return false;
            }

            @Override
            public String getString(String key, Object... arguments) {
                return null;
            }

            @Override
            public String getString(String key) {
                return null;
            }
        };
    }
}
