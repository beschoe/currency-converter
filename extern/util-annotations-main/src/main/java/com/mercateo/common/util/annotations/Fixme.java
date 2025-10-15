/*
 * Created on 18 Jan 2023
 *
 * author dimitry
 */
package com.mercateo.common.util.annotations;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;

@Retention(SOURCE)
public @interface Fixme {
    enum ToDo {REMOVE, MOVE, MAKE_NON_NULL}
    ToDo value();
}
