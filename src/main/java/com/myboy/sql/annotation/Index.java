package com.myboy.sql.annotation;


import com.myboy.sql.constant.IndexEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 索引注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Index {

    /**
     * 索引名称
     */
    String name();

    /**
     * 索引类型
     */
    IndexEnum type() default IndexEnum.NORMAL;

    /**
     * 联合索引字段(包括本字段)
     */
    String[] fields() default {};
}
