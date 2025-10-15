package com.mercateo.common.i18n;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class CountryCode2GeographicalAreaConverter0Test {
    @Test
    public void testConvertValidCountryCode() throws Exception {
        final String countryCodeAT = "AT";
        final GeographicalArea at = CountryCode2GeographicalAreaConverter.convert(countryCodeAT);
        Assert.assertNotNull(at);
        Assert.assertEquals(countryCodeAT, at.iso3166alpha2UpperCaseCode);
    }

    @Test
    public void testConvertEmptyCountryCode() throws Exception {
        final GeographicalArea geographicalArea = CountryCode2GeographicalAreaConverter.convert(
                " ");
        Assert.assertNotNull(geographicalArea);
        Assert.assertEquals(GeographicalArea.DEFAULT_de, geographicalArea);
    }

    @Test
    public void testConvertNullCountryCode() throws Exception {
        final GeographicalArea geographicalArea = CountryCode2GeographicalAreaConverter.convert(
                null);
        Assert.assertNotNull(geographicalArea);
        Assert.assertEquals(GeographicalArea.DEFAULT_de, geographicalArea);
    }

    @Test
    public void testConvertUnsupportedCountryCode() throws Exception {
        final String unsupportedCountryCode = "RU";
        final GeographicalArea geographicalArea = CountryCode2GeographicalAreaConverter.convert(
                unsupportedCountryCode);
        Assert.assertNotNull(geographicalArea);
        Assert.assertEquals(GeographicalArea.DEFAULT_de, geographicalArea);
    }

}
