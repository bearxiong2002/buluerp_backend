package com.ruoyi.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于在特定业务方法执行前，将相关通知标记为已读。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MarkNotificationsAsRead {

    /**
     * 业务类型，例如 "order", "production"等。
     * @return 业务类型字符串
     */
    String businessType();

    /**
     * 用于获取业务ID集合的SpEL表达式。
     * 表达式的计算结果应该是一个 `java.util.Collection` 类型的对象，其中包含ID。
     * 例如，如果方法参数是 `(Request req)` 且 `req.getIds()` 返回 `List<Long>`，表达式可以是 `#req.getIds()`。
     * @return SpEL表达式字符串
     */
    String businessIdsExpression();
} 