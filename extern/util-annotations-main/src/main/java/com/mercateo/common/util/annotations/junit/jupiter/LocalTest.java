/*
 * Created on 7 Nov 2024
 *
 * author dimitry
 */
package com.mercateo.common.util.annotations.junit.jupiter;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import com.mercateo.common.util.annotations.junit.LocalTests;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for marking locally executed tests in JUnit 5.
 * <p>
 * Tests marked with this annotation are tagged as "LocalTests" and should be excluded from CI builds with Maven profiles
 * using the group "com.mercateo.common.util.annotations.junit.LocalTests".
 * </p>
 * <p>
 * The {@code value} field should contain a reason explaining why the test is limited to local execution.
 * This reason can help developers understand the instability or special requirements of the test.
 * </p>
 */
@Test
@Tag(LocalTests.TAG) // JUnit 5 tag for flaky tests
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface LocalTest {
    /**
     * Specifies the reason why the test is marked as IDE-only.
     *
     * @return a description of the reason for IDE-only tagging
     */
    String value();
}