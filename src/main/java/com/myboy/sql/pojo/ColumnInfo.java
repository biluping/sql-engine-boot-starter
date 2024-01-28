package com.myboy.sql.pojo;


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

    public String getCOLUMN_NAME() {
        return COLUMN_NAME;
    }

    public void setCOLUMN_NAME(String COLUMN_NAME) {
        this.COLUMN_NAME = COLUMN_NAME;
    }

    public Integer getCHARACTER_MAXIMUM_LENGTH() {
        return CHARACTER_MAXIMUM_LENGTH;
    }

    public void setCHARACTER_MAXIMUM_LENGTH(Integer CHARACTER_MAXIMUM_LENGTH) {
        this.CHARACTER_MAXIMUM_LENGTH = CHARACTER_MAXIMUM_LENGTH;
    }

    public String getCOLUMN_TYPE() {
        return COLUMN_TYPE;
    }

    public void setCOLUMN_TYPE(String COLUMN_TYPE) {
        this.COLUMN_TYPE = COLUMN_TYPE;
    }

    public String getDATA_TYPE() {
        return DATA_TYPE;
    }

    public void setDATA_TYPE(String DATA_TYPE) {
        this.DATA_TYPE = DATA_TYPE;
    }

    public Integer getNUMERIC_PRECISION() {
        return NUMERIC_PRECISION;
    }

    public void setNUMERIC_PRECISION(Integer NUMERIC_PRECISION) {
        this.NUMERIC_PRECISION = NUMERIC_PRECISION;
    }

    public Integer getNUMERIC_SCALE() {
        return NUMERIC_SCALE;
    }

    public void setNUMERIC_SCALE(Integer NUMERIC_SCALE) {
        this.NUMERIC_SCALE = NUMERIC_SCALE;
    }

    public String getIS_NULLABLE() {
        return IS_NULLABLE;
    }

    public void setIS_NULLABLE(String IS_NULLABLE) {
        this.IS_NULLABLE = IS_NULLABLE;
    }

    public String getCOLUMN_DEFAULT() {
        return COLUMN_DEFAULT;
    }

    public void setCOLUMN_DEFAULT(String COLUMN_DEFAULT) {
        this.COLUMN_DEFAULT = COLUMN_DEFAULT;
    }

    public String getCOLUMN_COMMENT() {
        return COLUMN_COMMENT;
    }

    public void setCOLUMN_COMMENT(String COLUMN_COMMENT) {
        this.COLUMN_COMMENT = COLUMN_COMMENT;
    }

    public String getCOLUMN_KEY() {
        return COLUMN_KEY;
    }

    public void setCOLUMN_KEY(String COLUMN_KEY) {
        this.COLUMN_KEY = COLUMN_KEY;
    }

    public String getEXTRA() {
        return EXTRA;
    }

    public void setEXTRA(String EXTRA) {
        this.EXTRA = EXTRA;
    }
}
