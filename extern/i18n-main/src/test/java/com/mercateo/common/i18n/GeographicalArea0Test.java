package com.mercateo.common.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Currency;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import junit.framework.TestCase;

@SuppressWarnings("null")
@RunWith(DataProviderRunner.class)
public class GeographicalArea0Test extends TestCase {

    // @formatter:off
    @DataProvider
    public static Object[][] serverNameToGeographicalArea() {
        return new Object[][]{
                { "abc.def",            GeographicalArea.DEFAULT_de },
                { "mercateo.xx",        GeographicalArea.DEFAULT_de },
                { "xx.mercateo.com",    GeographicalArea.DEFAULT_de },
                { "mercateo.com",       GeographicalArea.DEFAULT_de },
                { "mercateo.fr",        GeographicalArea.fr },
                { "mercateo.co.uk",     GeographicalArea.gb },
                { null,                 GeographicalArea.DEFAULT_de },
                { "franklin9",          GeographicalArea.DEFAULT_de },
                { "www.mercateo.com",   GeographicalArea.DEFAULT_de },
                { "www.mercateo.de",    GeographicalArea.de },
                { "de.mercateo.com",    GeographicalArea.de },
                { "www.mercateo.at",    GeographicalArea.at },
                { "at.mercateo.com",    GeographicalArea.at },
                { "www.mercateo.co.uk", GeographicalArea.gb },
                { "www.mercateo.be",    GeographicalArea.be },
                { "be.mercateo.com",    GeographicalArea.be },
                { "www.mercateo.cz",    GeographicalArea.cz },
                { "cz.mercateo.com",    GeographicalArea.cz },
                { "www.mercateo.es",    GeographicalArea.es },
                { "es.mercateo.com",    GeographicalArea.es },
                { "www.mercateo.fr",    GeographicalArea.fr },
                { "fr.mercateo.com",    GeographicalArea.fr },
                { "www.mercateo.hu",    GeographicalArea.hu },
                { "hu.mercateo.com",    GeographicalArea.hu },
                { "www.mercateo.ie",    GeographicalArea.ie },
                { "ie.mercateo.com",    GeographicalArea.ie },
                { "www.mercateo.it",    GeographicalArea.it },
                { "it.mercateo.com",    GeographicalArea.it },
                { "www.mercateo.lu",    GeographicalArea.lu },
                { "lu.mercateo.com",    GeographicalArea.lu },
                { "www.mercateo.nl",    GeographicalArea.nl },
                { "nl.mercateo.com",    GeographicalArea.nl },
                { "www.mercateo.pl",    GeographicalArea.pl },
                { "pl.mercateo.com",    GeographicalArea.pl },
                { "www.mercateo.se",    GeographicalArea.se },
                { "se.mercateo.com",    GeographicalArea.se },
                { "www.mercateo.sk",    GeographicalArea.sk },
                { "sk.mercateo.com",    GeographicalArea.sk },
                { "0.0.0.0",            GeographicalArea.de },
                { "127.0.0.1",          GeographicalArea.de },
                { "10.155.12.87",       GeographicalArea.de },
                { "test.mercateo.co.uk",GeographicalArea.gb },
                { "test.mercateo.at",   GeographicalArea.at },
        };
    }
    // @formatter:on

    @UseDataProvider("serverNameToGeographicalArea")
    @Test
    public void testExtractGeographicalAreaFromServerName(String serverName,
            GeographicalArea correctGeographicalArea) {
        GeographicalArea geographicalArea = GeographicalArea.extractGeographicalAreaFromServerName(
                serverName);
        assertThat(geographicalArea).isEqualTo(correctGeographicalArea);
    }

    // @formatter:off
    @DataProvider
    public static Object[][] currencyInGeographicalAreas() {
        return new Object[][]{
                { GeographicalArea.de, KnownCurrencies.EUR.getCurrencyCode() },
                { GeographicalArea.at, KnownCurrencies.EUR.getCurrencyCode() },
                { GeographicalArea.ch, KnownCurrencies.CHF.getCurrencyCode() },
                { GeographicalArea.cz, KnownCurrencies.CZK.getCurrencyCode() },
                { GeographicalArea.gb, KnownCurrencies.GBP.getCurrencyCode() },
                { GeographicalArea.es, KnownCurrencies.EUR.getCurrencyCode() },
                { GeographicalArea.fr, KnownCurrencies.EUR.getCurrencyCode() },
                { GeographicalArea.ie, KnownCurrencies.EUR.getCurrencyCode() },
                { GeographicalArea.it, KnownCurrencies.EUR.getCurrencyCode() },
                { GeographicalArea.nl, KnownCurrencies.EUR.getCurrencyCode() },
                { GeographicalArea.pl, KnownCurrencies.PLN.getCurrencyCode() },
                { GeographicalArea.hu, KnownCurrencies.HUF.getCurrencyCode() },
                { GeographicalArea.sk, KnownCurrencies.EUR.getCurrencyCode() },
                { GeographicalArea.be, KnownCurrencies.EUR.getCurrencyCode() },
                { GeographicalArea.lu, KnownCurrencies.EUR.getCurrencyCode() },
                { GeographicalArea.se, KnownCurrencies.SEK.getCurrencyCode() },
                { GeographicalArea.cn, KnownCurrencies.CNY.getCurrencyCode() },
                { GeographicalArea.in, KnownCurrencies.INR.getCurrencyCode() },
                { GeographicalArea.br, KnownCurrencies.BRL.getCurrencyCode() },
                { GeographicalArea.mx, KnownCurrencies.MXN.getCurrencyCode() },
                { GeographicalArea.dk, KnownCurrencies.DKK.getCurrencyCode() },
                { GeographicalArea.us, KnownCurrencies.USD.getCurrencyCode() },
                { GeographicalArea.lv, KnownCurrencies.EUR.getCurrencyCode() },
                { GeographicalArea.ee, KnownCurrencies.EUR.getCurrencyCode() },
        };
    }
    // @formatter:on

    @UseDataProvider("currencyInGeographicalAreas")
    @Test
    public void testCurrency(GeographicalArea geographicalArea, String correctCurrencyCode) {
        final Currency currency = geographicalArea.getLocaleInfosBean().currency;
        final String currencyCode = currency.getCurrencyCode();
        assertThat(currencyCode).isEqualTo(correctCurrencyCode);
    }

    // @formatter:off
    @DataProvider
    public static Object[] geographicalAreas() {
        return GeographicalArea.values();
    }
    // @formatter:on

    @UseDataProvider("geographicalAreas")
    @Test
    public void testLanguagesAreNotNull(GeographicalArea geographicalArea) {
        SoftAssertions softly = new SoftAssertions();
        for (Language language : geographicalArea.languages) {
            softly.assertThat(language).as("GeographicalArea %s contains null language",
                    geographicalArea).isNotNull();
        }
        softly.assertAll();
    }

    @UseDataProvider("geographicalAreas")
    @Test
    public void testLanguagesAreNotEmpty(GeographicalArea geographicalArea) {
        assertThat(geographicalArea.languages).as(
                "GeographicalArea must support at least 1 language").isNotEmpty();
    }

    // @formatter:off
    @DataProvider
    public static Object[][] countriesInDefaultLanguage() {
        return new Object[][]{
                { GeographicalArea.ie, "Ireland" },
                { GeographicalArea.gb, "United Kingdom" },
                { GeographicalArea.cz, "Česká republika", "Česko" },
                { GeographicalArea.de, "Deutschland" },
                { GeographicalArea.ch, "Schweiz" },
                { GeographicalArea.at, "Österreich" },
                { GeographicalArea.es, "Espa\u00F1a" },
                { GeographicalArea.fr, "France" },
                { GeographicalArea.nl, "Nederland" },
                { GeographicalArea.be, "België" },
                { GeographicalArea.hu, "Magyarorsz\u00E1g" },
                { GeographicalArea.it, "Italia" },
                { GeographicalArea.pl, "Polska" },
                { GeographicalArea.sk, "Slovenská republika", "Slovensko" },
        };
    }
    // @formatter:on

    @UseDataProvider("countriesInDefaultLanguage")
    @Test
    public void testGetNameInDefaultLanguage(GeographicalArea country,
            String... acceptedTranslations) {
        String nameInDefaultLanguage = country.getNameInDefaultLanguage();
        assertThat(nameInDefaultLanguage).isIn((Object[]) acceptedTranslations);
    }

    // @formatter:off
    @DataProvider
    public static Object[][] countriesInLanguage() {
        return new Object[][]{
                { GeographicalArea.ch, Language.ITALIAN, "Svizzera" },
                { GeographicalArea.be, Language.FRENCH,  "Belgique" },
                { GeographicalArea.ch, Language.FRENCH,  "Suisse" },
                { GeographicalArea.gb, Language.GERMAN,  "Vereinigtes Königreich" },
                { GeographicalArea.de, Language.DUTCH,   "Duitsland" },
                { GeographicalArea.at, Language.ENGLISH, "Austria" },
                { GeographicalArea.be, Language.ENGLISH, "Belgium" },
                { GeographicalArea.cz, Language.ENGLISH, "Czech Republic", "Czechia" },
                { GeographicalArea.fr, Language.ENGLISH, "France" },
                { GeographicalArea.de, Language.ENGLISH, "Germany" },
                { GeographicalArea.hu, Language.ENGLISH, "Hungary" },
                { GeographicalArea.it, Language.ENGLISH, "Italy" },
                { GeographicalArea.lu, Language.ENGLISH, "Luxembourg" },
                { GeographicalArea.nl, Language.ENGLISH, "Netherlands" },
                { GeographicalArea.pl, Language.ENGLISH, "Poland" },
                { GeographicalArea.sk, Language.ENGLISH, "Slovakia" },
                { GeographicalArea.es, Language.ENGLISH, "Spain" },
                { GeographicalArea.se, Language.ENGLISH, "Sweden" },
                { GeographicalArea.ch, Language.ENGLISH, "Switzerland" },
        };
    }
    // @formatter:on

    @UseDataProvider("countriesInLanguage")
    @Test
    public void testGetNameInLanguage(GeographicalArea country, Language language,
            String... acceptedTranslations) {
        String nameInLanguage = country.getNameInLanguage(language);
        assertThat(nameInLanguage).isIn((Object[]) acceptedTranslations);
    }

    /**
     * Not a test - run this to get a list of country and primary languages.
     * 
     * Used to generate the list in https://jira.mercateo.lan/browse/ITHD-41207
     * 
     * @param args
     */
    public static void main(String args[]) {
        for (GeographicalArea geographicalArea : GeographicalArea.values()) {
            System.out.println(geographicalArea + ":");
            for (Language language2 : geographicalArea.languages) {
                System.out.println("    " + language2 + ": " + language2.isoLanguage3
                        + (language2.isoLanguage3B == null ? ""
                                : (" / " + language2.isoLanguage3B)));
            }
            System.out.println();
        }
    }

}
