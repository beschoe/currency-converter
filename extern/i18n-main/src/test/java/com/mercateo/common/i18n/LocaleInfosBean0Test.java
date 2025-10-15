/*
 * Created on 29.04.2015
 *
 * author joerg_adler
 */
package com.mercateo.common.i18n;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LocaleInfosBean0Test {
    @Test
    public void TestGbZipPattern() {
        assertTrue(LocaleInfosBean.ZIP_PATTERN_gb.matcher("A1 2AA").matches());
        assertTrue(LocaleInfosBean.ZIP_PATTERN_gb.matcher("A12AA").matches());

        assertTrue(LocaleInfosBean.ZIP_PATTERN_gb.matcher("A11 2AA").matches());
        assertTrue(LocaleInfosBean.ZIP_PATTERN_gb.matcher("A112AA").matches());

        assertTrue(LocaleInfosBean.ZIP_PATTERN_gb.matcher("AA11 2AA").matches());
        assertTrue(LocaleInfosBean.ZIP_PATTERN_gb.matcher("AA112AA").matches());

        assertTrue(LocaleInfosBean.ZIP_PATTERN_gb.matcher("A1A 2AA").matches());
        assertTrue(LocaleInfosBean.ZIP_PATTERN_gb.matcher("A1A2AA").matches());

        assertTrue(LocaleInfosBean.ZIP_PATTERN_gb.matcher("AA2A 2AA").matches());
        assertTrue(LocaleInfosBean.ZIP_PATTERN_gb.matcher("AA2A2AA").matches());

        assertTrue(LocaleInfosBean.ZIP_PATTERN_gb.matcher("DE56 2RA").matches());

        assertTrue(LocaleInfosBean.ZIP_PATTERN_lv.matcher("1234").matches());
        assertTrue(LocaleInfosBean.ZIP_PATTERN_ee.matcher("12345").matches());
    }
}
