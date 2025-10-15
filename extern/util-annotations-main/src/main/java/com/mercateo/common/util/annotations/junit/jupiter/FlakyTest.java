/*
 * Created on 7 Nov 2024
 *
 * author dimitry
 */
package com.mercateo.common.util.annotations.junit.jupiter;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import com.mercateo.common.util.annotations.junit.FlakyTests;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for marking flaky tests in JUnit 5.
 * <p>
 * Tests marked with this annotation are tagged as "FlakyTests" and can be included or excluded with Maven profiles
 * using the group "com.mercateo.common.util.annotations.junit.FlakyTests".
 * </p>
 * <p>
 * The {@code value} field is required to provide a reason explaining why the test is considered flaky.
 * This information helps developers understand the instability or specific conditions affecting the test's reliability.
 * </p>
 */
@Test
@Tag(FlakyTests.TAG) // JUnit 5 tag for flaky tests
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface FlakyTest {
    /**
     * Specifies the reason why the test is marked as flaky.
     *
     * @return a description of the reason for the flaky designation
     */
    String value();
}