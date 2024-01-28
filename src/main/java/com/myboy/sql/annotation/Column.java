package com.myboy.sql.annotation;

import com.myboy.sql.constant.MySqlTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在实体类的字段上，用于生成 sql
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    /**
     * 是否主键
     */
    boolean isKey() default false;

    /**
     * 是否自增
     */
    boolean autoIncrement() default false;

    /**
     * 列名, 默认是字段的下划线形式
     */
    String name() default "";

    /**
     * 长度
     */
    int len() default 64;

    /**
     * 小数位长度，decimal 类型会用到
     */
    int scale() default 2;

    /**
     * 是否非空
     */
    boolean notNull() default true;

    /**
     * 默认值
     */
    String defaultVal() default "";

    /**
     * 字段注释
     */
    String comment() default "";

    /**
     * jdbc 类型
     * 不指定则使用默认值
     */
    MySqlTypeEnum jdbcType() default MySqlTypeEnum.DEFAULT;
}
