package com.mercateo.common.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a Constructor as only existent for ByteCode-Manipulation only and
 * should not be called directly.
 * 
 * AOP frameworks need to be able to subclass in order to create proxies for
 * non-interface classes.
 * 
 * This annotation should always be used together with @Deprecated to signal
 * that this Constructor is not to be called manually.
 * 
 * <a href="http://confluence.mercateo.lan/x/7DxiAQ">Confluence Page</a>
 * 
 * Retention is RUNTIME in order to be able to reflect this data at build-time
 * to test possible rules/constraints.
 * 
 * @author usr / tby
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface VisibleForBytecodeEnhancement {
    // empty body
}
