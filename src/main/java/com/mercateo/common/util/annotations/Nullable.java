package com.mercateo.common.util.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(SOURCE)
@Target({ TYPE_USE, TYPE_PARAMETER, FIELD, METHOD, PARAMETER, LOCAL_VARIABLE })
public @interface Nullable {}
