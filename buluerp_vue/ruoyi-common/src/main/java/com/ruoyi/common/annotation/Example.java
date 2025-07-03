package com.ruoyi.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface Example {
    public static final String GEN_UUID = "EXAMPLE_GEN_UUID";

    String value() default "";
}
