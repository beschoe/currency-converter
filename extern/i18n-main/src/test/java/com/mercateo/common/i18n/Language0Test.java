package com.mercateo.common.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class Language0Test {

    // @formatter:off
    @DataProvider
    public static Object[][] languages() {
        return new Object[][] {
                { Language.GERMAN, "Deutsch" },
                { Language.ENGLISH, "English" },
                { Language.DUTCH, "Nederlands" },
                { Language.POLISH, "polski" },
        };
    }
    // @formatter:on

    @UseDataProvider("languages")
    @Test
    public void getNameInNativeLanguage(Language language, String expectedTranslation) {
        String nameInNativeLanguage = language.getNameInNativeLanguage();
        assertThat(nameInNativeLanguage).isEqualTo(expectedTranslation);
    }
}