package com.mercateo.common.i18n;

import com.mercateo.common.util.annotations.NonNull;
import com.mercateo.common.util.annotations.Nullable;

/**
 * Converts given <a href="http://en.wikipedia.org/wiki/ISO_3166">ISO 3166
 * alpha-2 country code</a> to the {@link GeographicalArea}. <br>
 * If countryCode is not a valid ISO 3166 code, or GeographicalArea for this
 * countryCode is not supported, then returns default GeographicalArea.
 * 
 * @see GeographicalArea#DEFAULT_de
 */
public class CountryCode2GeographicalAreaConverter {
    private CountryCode2GeographicalAreaConverter() {
        // no instance needed
    }

    /**
     * Converts given <a href="http://en.wikipedia.org/wiki/ISO_3166">ISO 3166
     * alpha-2 country code</a> to the {@link GeographicalArea}. <br>
     * If countryCode is not a valid ISO 3166 code, or GeographicalArea for this
     * countryCode is not supported, then returns default GeographicalArea.
     * 
     * @param countryCode
     * @return GeographicalArea
     * 
     * @see GeographicalArea#DEFAULT_de
     */
    @NonNull
    public static GeographicalArea convert(@Nullable final String countryCode) {
        GeographicalArea geographicalArea = null;

        if (countryCode != null) {
            geographicalArea = GeographicalArea.getGeographicalAreaByUpperCaseCountryCode(
                    countryCode);
        }

        if (geographicalArea == null) {
            geographicalArea = GeographicalArea.DEFAULT_de;
        }

        return geographicalArea;
    }
}