/*
 * Created on 20.09.2007
 *
 * author felix
 */
package com.mercateo.common.i18n;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

import com.mercateo.common.util.annotations.NonNullByDefault;
import com.mercateo.common.util.annotations.Nullable;

@NonNullByDefault
public interface MercateoLocalizer {

    String THIS_METHOD_HAS_NOT_BEEN_IMPLEMENTED_YET = "This method has not been implemented by the current MercateoLocalizer instance";

    DecimalFormat getPriceFormatter();

    DecimalFormat getQuantityFormatter();

    /**
     * Returns a localized string based on the given <code>key</code>. The localized
     * string is looked up in property resource files.
     * <p>
     * If problems occur, an appropriate non-null default string is returned.
     * 
     * @param key
     * @return the localized string or a non-null default string
     */
    String getString(String key);

    /**
     * Should avoid using this method to implement logic which depends on the
     * specific <code>key</code> exists, since key should eventually translated into
     * all supported languages. If you come to this solution, please reconsider the
     * decision.
     * @param key 
     * @return boolean
     */
    boolean hasString(String key);

    /**
     * Same as {@link #getString(String)}, but formats the localized string with a
     * <code>MessageFormat</code>.
     * 
     * @param key
     * @param arguments
     * @return DOCUMENT_ME
     */
    String getString(String key, Object... arguments);

    /**
     * Shorthand method for printing localized strings in JSPs. National letters are
     * escaped, but SGML meta characters (less, ampersand, ...) are not.
     * 
     * @param key
     * @return DOCUMENT_ME
     */
    String ls(String key);

    /**
     * Shorthand method for printing localized strings in JSPs. National letters are
     * escaped, but SGML meta characters (less, ampersand, ...) are not.
     * 
     * @param key
     * @param arguments
     *            Substitution arguments, see <code>java.text.MessageFormat</code>.
     * @return DOCUMENT_ME
     */
    String ls(String key, Object... arguments);

    /**
     * Method for printing localized strings for GeoAreaSpecificMessage in JSPs.
     * National letters are escaped, but SGML meta characters (less, ampersand, ...)
     * are not.
     *
     * @param key
     *            the message key which follows &quot;<strong>
     *            &lt;key&gt;_&lt;GEO_AREA_CODE&gt;</strong> &quot; convention
     * @return Geographical area specific message.
     */
    @SuppressWarnings("unused")
    default String getEscapedGeoAreaMsg(String key) {
        throw new UnsupportedOperationException(THIS_METHOD_HAS_NOT_BEEN_IMPLEMENTED_YET);
    }

    /**
     * Shorthand method for printing localized strings for GeoAreaSpecificMessage in
     * JSPs. National letters are escaped, but SGML meta characters (less,
     * ampersand, ...) are not.
     *
     * @param key
     *            the message key which follows &quot;<strong>
     *            &lt;key&gt;_&lt;GEO_AREA_CODE&gt;</strong> &quot; convention
     *
     * @param args
     *            Substitution arguments, see <code>java.text.MessageFormat</code>.
     * @return Geographical area specific message.
     */
    @SuppressWarnings("unused")
    default String getEscapedGeoAreaMsg(String key, Object... args) {
        throw new UnsupportedOperationException(THIS_METHOD_HAS_NOT_BEEN_IMPLEMENTED_YET);
    }

    /**
     * Shorthand method for printing localized strings in JSPs. Special characters
     * are not escaped and are returned "as they are". (The "r" in the method name
     * stands for "raw".)
     * 
     * @param key
     * @return DOCUMENT_ME
     */
    String rls(String key);

    /**
     * Shorthand method for printing localized strings in JSPs. Special characters
     * are not escaped and are returned "as they are". (The "r" in the method name
     * stands for "raw".)
     * 
     * @param key
     * @param arguments
     *            Substitution arguments, see <code>java.text.MessageFormat</code>.
     * @return DOCUMENT_ME
     */
    String rls(String key, Object... arguments);

    /**
     * Shorthand method for formatting a quantity.
     * 
     * @param value
     * @return DOCUMENT_ME
     */
    String lq(double value);

    /**
     * Shorthand method for formatting a quantity.
     * 
     * @param value
     * @return DOCUMENT_ME
     */
    String lq(Number value);

    /**
     * @return DOCUMENT_ME
     */
    Locale getLocale();

    /**
     * Localize Unit
     * 
     * @param unit
     * @param printPreview
     * @param singular
     * @return String
     */
    String lu(String unit, boolean printPreview, boolean singular);

    /**
     * Note: The result is NOT ESCAPED! It might e.g. contain the character '\u00a0'
     * as grouping separator in France.
     * 
     * @param bigDecimal
     * @return formatted String
     */
    String formatAsPriceAmount(@Nullable BigDecimal bigDecimal);

    /**
     * Some information which the system needs to show on pages like invoice
     * summary, order information, basket page etc., is the legally obligatory
     * information which is dependent on the shop region. This method addresses such
     * messages.
     * <p>
     * Note that the method currently does not ensure that the message is fetched
     * automatically regarding the client's shop. Message keys, which are passed to
     * this method, are supposed to follow &quot;<strong>
     * &lt;key&gt;_&lt;GEO_AREA_CODE&gt;</strong> &quot; convention.
     * </p>
     * 
     * @param key
     *            the message key which follows &quot;<strong>
     *            &lt;key&gt;_&lt;GEO_AREA_CODE&gt;</strong> &quot; convention
     * @return Geographical area specific message
     */
    @SuppressWarnings("unused")
    default String getGeoAreaSpecificMessage(String key) {
        throw new UnsupportedOperationException(THIS_METHOD_HAS_NOT_BEEN_IMPLEMENTED_YET);
    }

    /**
     * Some information which the system needs to show on pages like invoice
     * summary, order information, basket page etc., is the legally obligatory
     * information which is dependent on the shop region. This method addresses such
     * messages.
     * <p>
     * Note that the method currently does not ensure that the message is fetched
     * automatically regarding the client's shop. Message keys, which are passed to
     * this method, are supposed to follow &quot;<strong>
     * &lt;key&gt;_&lt;GEO_AREA_CODE&gt;</strong> &quot; convention.
     * </p>
     *
     * @param key
     *            the message key which follows &quot;<strong>
     *            &lt;key&gt;_&lt;GEO_AREA_CODE&gt;</strong> &quot; convention
     * @param args
     *            dynamic arguments that will be replaced in the message template.
     * @return Geographical area specific message.
     */
    @SuppressWarnings("unused")
    default String getGeoAreaSpecificMessage(String key, Object... args) {
        throw new UnsupportedOperationException(THIS_METHOD_HAS_NOT_BEEN_IMPLEMENTED_YET);
    }
}