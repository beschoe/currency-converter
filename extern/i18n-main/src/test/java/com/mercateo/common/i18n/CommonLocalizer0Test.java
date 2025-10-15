package com.mercateo.common.i18n;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@SuppressWarnings("null")
@RunWith(DataProviderRunner.class)
public class CommonLocalizer0Test {

    @Test
    public void ls_should_be_able_to_render_complex_arguments_for_MessageFormat() {
        CommonLocalizer localizer = getCommonLocalizer(GeographicalArea.de, Language.GERMAN);
        assertThat(localizer.ls("complex_argument", 2)).isEqualTo("2 Tage");
    }

    @Test
    public void ls_should_only_render_param_which_present_in_value() {
        CommonLocalizer commonLocalizerEN = getCommonLocalizer(GeographicalArea.gb,
                Language.ENGLISH);
        assertEquals("Should only render param0:0 even contains multiple arguments",
                commonLocalizerEN.ls("fooSingleArgs", "0", "1"));
    }

    @Test
    public void getGeoAreaSpecificMessage_should_return_same_country_specific_info_for_different_languages() {
        final String fooLegalKey = "foo";
        CommonLocalizer commonLocalizerDE = getCommonLocalizer(GeographicalArea.de,
                Language.GERMAN);
        assertEquals("Foo rechtlicher Hinweis Deutschland", commonLocalizerDE
                .getGeoAreaSpecificMessage(fooLegalKey));
        CommonLocalizer commonLocalizerEN_DE = getCommonLocalizer(GeographicalArea.de,
                Language.ENGLISH);
        assertEquals("Foo legal hint Germany", commonLocalizerEN_DE.getGeoAreaSpecificMessage(
                fooLegalKey));
    }

    @Test
    public void getGeoAreaSpecificMessage_should_return_different_country_specific_info_for_different_countries() {
        final String fooLegalKey = "foo";
        CommonLocalizer commonLocalizerDE = getCommonLocalizer(GeographicalArea.de,
                Language.GERMAN);
        CommonLocalizer commonLocalizerAT = getCommonLocalizer(GeographicalArea.at,
                Language.GERMAN);
        assertNotEquals(commonLocalizerDE.getGeoAreaSpecificMessage(fooLegalKey), commonLocalizerAT
                .getGeoAreaSpecificMessage(fooLegalKey));
    }

    @Test
    public void getGeoAreaSpecificMessageWithArgs_should_return_different_country_specific_info_for_different_countries() {
        final String fooLegalKeyArgs = "fooArgs";
        int arg = 2;
        CommonLocalizer commonLocalizerDE = getCommonLocalizer(GeographicalArea.de,
                Language.GERMAN);
        CommonLocalizer commonLocalizerAT = getCommonLocalizer(GeographicalArea.at,
                Language.GERMAN);
        assertNotEquals(commonLocalizerDE.getGeoAreaSpecificMessage(fooLegalKeyArgs, arg),
                commonLocalizerAT.getGeoAreaSpecificMessage(fooLegalKeyArgs, arg));
    }

    @Test
    public void getGeoAreaSpecificMessageWithArgs_should_return_same_country_specific_info_for_different_languages() {
        final String fooLegalKeyArgs = "fooArgs";
        int arg = 1;

        CommonLocalizer commonLocalizerDE = getCommonLocalizer(GeographicalArea.de,
                Language.GERMAN);
        assertEquals("Foo rechtlicher Hinweis Deutschland 1", commonLocalizerDE
                .getGeoAreaSpecificMessage(fooLegalKeyArgs, arg));

        CommonLocalizer commonLocalizerEN_DE = getCommonLocalizer(GeographicalArea.de,
                Language.ENGLISH);
        assertEquals("Foo legal hint Germany 1", commonLocalizerEN_DE.getGeoAreaSpecificMessage(
                fooLegalKeyArgs, arg));
    }

    @Test(expected = IllegalStateException.class)
    public void getGeoAreaSpecificMessage_should_throw_exception_when_geo_are_null() {
        Locale locale = new Locale("de", "DE");
        ResourceBundle resourceBundle = ResourceBundle.getBundle("foo", locale);
        CommonLocalizer commonLocalizer = new CommonLocalizer(locale, null, null, null,
                resourceBundle, resourceBundle, Language.GERMAN, false);
        commonLocalizer.getGeoAreaSpecificMessage("foo");
    }

    @Test(expected = IllegalStateException.class)
    public void getGeoAreaSpecificMessage_with_args_should_throw_exception_when_geo_area_is_null() {
        Locale locale = new Locale("de", "DE");
        ResourceBundle resourceBundle = ResourceBundle.getBundle("foo", locale);
        CommonLocalizer commonLocalizer = new CommonLocalizer(locale, null, null, null,
                resourceBundle, resourceBundle, Language.GERMAN, false);
        commonLocalizer.getGeoAreaSpecificMessage("foo");
    }

    @Test
    public void getEscapedGeoAreaMsg_should_escape_characters_to_SGML() {
        CommonLocalizer commonLocalizerDE = getCommonLocalizer(GeographicalArea.de,
                Language.GERMAN);
        assertEquals("&#214;&#246;&#220;&#252;&#196;&#228;&#223; in Deutschland", commonLocalizerDE
                .getEscapedGeoAreaMsg("escape"));
    }

    @Test
    public void getEscapedGeoAreaMsgWithArgs_should_escape_characters_to_SGML() {
        CommonLocalizer commonLocalizerDE = getCommonLocalizer(GeographicalArea.de,
                Language.GERMAN);
        assertEquals("&#214;&#246;&#220;&#252;&#196;&#228;&#223; in Deutschland", commonLocalizerDE
                .getEscapedGeoAreaMsg("escapeArgs", "ÖöÜüÄäß"));
    }

    @Test
    public void getString_should_render_single_quote_correctly() {
        CommonLocalizer commonLocalizerFR = getCommonLocalizer(GeographicalArea.de,
                Language.FRENCH);
        assertEquals("Fabricant d'imprimantes", commonLocalizerFR.getString("key.contains.quote"));
    }

    @Test
    public void getStringWithArgs_should_render_single_quote_in_key_without_params_correctly() {
        CommonLocalizer commonLocalizerFR = getCommonLocalizer(GeographicalArea.de,
                Language.FRENCH);
        assertEquals("Fabricant d'imprimantes", commonLocalizerFR.getString("key.contains.quote",
                "I'm misused"));
    }

    @Test
    public void getStringWithArgs_should_render_single_quote_correctly() {
        CommonLocalizer commonLocalizerFR = getCommonLocalizer(GeographicalArea.de,
                Language.FRENCH);
        assertEquals("Chercher par marque, produit 1 millions d'articles", commonLocalizerFR
                .getString("key.contains.quote.and.params", 1));
    }

    @Test
    public void getStringWithArgs_should_render_single_quote_in_params_correctly() {
        CommonLocalizer commonLocalizerFR = getCommonLocalizer(GeographicalArea.de,
                Language.FRENCH);
        assertEquals("Chercher par marque, produit 1'000 millions d'articles", commonLocalizerFR
                .getString("key.contains.quote.and.params", "1'000"));
    }

    @Test
    public void getStringWithArgs_should_render_single_quote_multiple_times_correctly() {
        CommonLocalizer commonLocalizerFR = getCommonLocalizer(GeographicalArea.de,
                Language.FRENCH);
        assertEquals("Demandes, 'n'a', d'articles", commonLocalizerFR.getString(
                "key.contains.multiple.quote.and.params", "n'a"));
    }

    @DataProvider
    public static Object[][] dataPriceFormat() {
        // @formatter:off
        return new Object[][] {
                { Double.MIN_VALUE, "0,00", GeographicalArea.DEFAULT_de},
                { 0.001, "0,00", GeographicalArea.DEFAULT_de },
                { 0.01, "0,01", GeographicalArea.DEFAULT_de },
                { 0.1, "0,10", GeographicalArea.DEFAULT_de },
                { 1, "1,00", GeographicalArea.DEFAULT_de },
                { 1000000, "1.000.000,00", GeographicalArea.DEFAULT_de },
                { Double.MAX_VALUE, "179.769.313.486.231.570.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000,00", GeographicalArea.DEFAULT_de},
                { 0.001, "0.00", GeographicalArea.gb },
                { 0.1, "0.10", GeographicalArea.gb },
                { 1, "1.00", GeographicalArea.gb },
                { 1000000, "1,000,000.00", GeographicalArea.gb }
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataPriceFormat")
    public void testPriceFormatter(double inputPrice, String expected,
            GeographicalArea geographicalArea) {
        CommonLocalizer loc = getCommonLocalizer(geographicalArea, geographicalArea.language);
        assertEquals(expected, loc.formatAsPriceAmount(BigDecimal.valueOf(inputPrice)));
    }

    @DataProvider
    public static Object[][] dataQuantityFormat() {
        // @formatter:off
        return new Object[][] {
                { Double.MIN_VALUE, "0", GeographicalArea.DEFAULT_de},
                { 0.001, "0,001", GeographicalArea.DEFAULT_de },
                { 0.01, "0,01", GeographicalArea.DEFAULT_de },
                { 0.1, "0,1", GeographicalArea.DEFAULT_de },
                { 1, "1", GeographicalArea.DEFAULT_de },
                { 1000000, "1.000.000", GeographicalArea.DEFAULT_de },
                { Double.MAX_VALUE, "179.769.313.486.231.570.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000", GeographicalArea.DEFAULT_de},
                { 0.001, "0.001", GeographicalArea.gb},
                { 0.1, "0.1", GeographicalArea.gb },
                { 1, "1", GeographicalArea.gb },
                { 1000000, "1,000,000", GeographicalArea.gb }
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataQuantityFormat")
    public void testQuantityFormatter(double inputQuantity, String expected,
            GeographicalArea geographicalArea) {
        CommonLocalizer loc = getCommonLocalizer(geographicalArea, geographicalArea.language);
        assertEquals(expected, loc.getQuantityFormatter().format(BigDecimal.valueOf(
                inputQuantity)));
    }

    private CommonLocalizer getCommonLocalizer(GeographicalArea geographicalArea,
            Language language) {
        String resourceBundleBaseName = "foo";
        Locale locale = new Locale(language.isoLanguage2,
                geographicalArea.iso3166alpha2UpperCaseCode);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceBundleBaseName, locale);
        return new CommonLocalizer(locale, geographicalArea, null, null, resourceBundle,
                resourceBundle, language, false);
    }
}