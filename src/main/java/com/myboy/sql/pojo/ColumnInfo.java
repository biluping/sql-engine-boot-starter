package com.myboy.sql.pojo;

import lombok.Data;

@Data
public class ColumnInfo {

    /**
     * 列名
     */
    private String COLUMN_NAME;

    /**
     * 字符最大长度
     */
    private Integer CHARACTER_MAXIMUM_LENGTH;

    /**
     * 类型 varchar(12)
     */
    private String COLUMN_TYPE;

    /**
     * 类型 varchar
     */
    private String DATA_TYPE;

    /**
     * 数字精度
     */
    private Integer NUMERIC_PRECISION;

    /**
     * 小数位数
     */
    private Integer NUMERIC_SCALE;

    /**
     * 是否可以为 null
     */
    private String IS_NULLABLE;

    /**
     * 默认值
     */
    private String COLUMN_DEFAULT;

    /**
     * 注释
     */
    private String COLUMN_COMMENT;

    /**
     * 索引标识，PRI UNI MUL
     */
    private String COLUMN_KEY;

    /**
     * auto_increment
     */
    private String EXTRA;

}
