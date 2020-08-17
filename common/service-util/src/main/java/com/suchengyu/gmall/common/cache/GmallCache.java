package com.suchengyu.gmall.common.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * GmallCache
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-22
 * @Description:
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GmallCache {
    /**
     *缓存key的前缀,建议自定义
     */
    String prefix() default "cache:";

    /**
     * 缓存key的后缀,建议自定义
     */
    String suffix() default ":suffix";
}
