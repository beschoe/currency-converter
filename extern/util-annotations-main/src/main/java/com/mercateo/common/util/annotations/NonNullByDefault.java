/*
 * Created on 02.07.2012
 *
 * author till
 */
package com.mercateo.common.util.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see <a href=
 *      "http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjdt%2Fannotation%2FNonNullByDefault.html">
 *      org.eclipse.jdt.annotation.NonNullByDefault</a>
 *
 *      Note: Modification for the MockitoNullChecker: Changed RententionPolicy
 *      from CLASS to RUNTIME.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR,
        ElementType.FIELD, ElementType.LOCAL_VARIABLE })
public @interface NonNullByDefault {
    DefaultLocation[] value() default { DefaultLocation.PARAMETER, DefaultLocation.RETURN_TYPE,
            DefaultLocation.FIELD, DefaultLocation.TYPE_ARGUMENT, DefaultLocation.TYPE_BOUND };
}
