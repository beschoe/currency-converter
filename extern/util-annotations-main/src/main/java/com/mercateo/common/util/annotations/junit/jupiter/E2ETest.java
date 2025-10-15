/*
 * Created on 7 Nov 2024
 *
 * author dimitry
 */
package com.mercateo.common.util.annotations.junit.jupiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.mercateo.common.util.annotations.junit.E2ETests;

/**
 * Custom annotation for marking end-to-end tests in JUnit 5.
 * <p>
 * Tests marked with this annotation are tagged as "E2ETests" and can be included or excluded with Maven profiles
 * using the group "com.mercateo.common.util.annotations.junit.E2ETests".
 * </p>
  */
@Test
@Tag(E2ETests.TAG) // JUnit 5 tag for end-to-end tests
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface E2ETest {/**/}