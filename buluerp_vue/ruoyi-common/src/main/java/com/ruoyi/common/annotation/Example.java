package com.ruoyi.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface Example {
    public static final String GEN_UUID = "844ede1d-1c68-4a4d-9338-e084a3f02ac5";
    public static final String CREATE_RECURSIVE = "d3b88e0e-6b3a-4c14-b071-65af0c6cfa98";

    String value() default "";

    // 集合类型的元素类型
    Class<?> elementType() default Object.class;
}
