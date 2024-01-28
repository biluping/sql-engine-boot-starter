package com.myboy.sql.util;

import com.myboy.sql.annotation.Column;
import com.myboy.sql.annotation.Index;
import com.myboy.sql.constant.IndexEnum;
import com.myboy.sql.constant.MySqlTypeEnum;
import com.myboy.sql.pojo.ColumnInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class ColUtil {

    private static final Logger log = LoggerFactory.getLogger(ColUtil.class);

    @Column
    private static final Map<Class<?>, MySqlTypeEnum> jdbcTypeMap = new HashMap<>();

    static {
        jdbcTypeMap.put(String.class, MySqlTypeEnum.VARCHAR);
        jdbcTypeMap.put(Long.class, MySqlTypeEnum.BIGINT);
        jdbcTypeMap.put(Integer.class, MySqlTypeEnum.INT);
        jdbcTypeMap.put(Boolean.class, MySqlTypeEnum.BIT);
        jdbcTypeMap.put(BigInteger.class, MySqlTypeEnum.BIGINT);
        jdbcTypeMap.put(Float.class, MySqlTypeEnum.FLOAT);
        jdbcTypeMap.put(Double.class, MySqlTypeEnum.DOUBLE);
        jdbcTypeMap.put(Short.class, MySqlTypeEnum.SMALLINT);
        jdbcTypeMap.put(BigDecimal.class, MySqlTypeEnum.DECIMAL);
        jdbcTypeMap.put(java.sql.Date.class, MySqlTypeEnum.DATE);
        jdbcTypeMap.put(Date.class, MySqlTypeEnum.DATETIME);
        jdbcTypeMap.put(Timestamp.class, MySqlTypeEnum.DATETIME);
        jdbcTypeMap.put(Time.class, MySqlTypeEnum.TIME);
        jdbcTypeMap.put(LocalDateTime.class, MySqlTypeEnum.DATETIME);
        jdbcTypeMap.put(LocalDate.class, MySqlTypeEnum.DATE);
        jdbcTypeMap.put(LocalTime.class, MySqlTypeEnum.TIME);
        jdbcTypeMap.put(long.class, MySqlTypeEnum.BIGINT);
        jdbcTypeMap.put(int.class, MySqlTypeEnum.INT);
        jdbcTypeMap.put(float.class, MySqlTypeEnum.FLOAT);
        jdbcTypeMap.put(double.class, MySqlTypeEnum.DOUBLE);
        jdbcTypeMap.put(short.class, MySqlTypeEnum.SMALLINT);
        jdbcTypeMap.put(char.class, MySqlTypeEnum.VARCHAR);
    }

    /**
     * 获取字段 sql
     */
    public static String getColumnSql(Field field, boolean needPrintPrimaryKey) {
        StringBuilder sb = new StringBuilder();
        String name = ColUtil.getName(field);
        String type = ColUtil.getType(field);
        String autoIncrement = ColUtil.getAutoIncrement(field);
        String notNull = ColUtil.getNotNull(field);
        String defaultVal = ColUtil.getDefaultVal(field);
        String comment = ColUtil.getComment(field);

        sb.append(name).append(" ")
                .append(type).append(" ");

        if (needPrintPrimaryKey) {
            String primaryKey = ColUtil.getPrimaryKey(field);
            sb.append(primaryKey).append(" ");
        }

        return sb
                .append(autoIncrement).append(" ")
                .append(notNull).append(" ")
                .append(defaultVal).append(" ")
                .append(comment)
                .toString();
    }

    private static String getAutoIncrement(Field field) {
        Column column = field.getAnnotation(Column.class);
        return column != null && column.autoIncrement() && column.isKey() ? "auto_increment" : "";
    }

    private static String getPrimaryKey(Field field) {
        Column column = field.getAnnotation(Column.class);
        return column != null && column.isKey() ? "primary key" : "";
    }


    /**
     * 获取列名
     */
    public static String getName(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !column.name().isEmpty()) {
            return column.name();
        }
        return StrUtil.toUnderline(field.getName());
    }

    /**
     * 获取列类型
     */
    public static String getType(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column == null) {
            try {
                column = ColUtil.class.getDeclaredField("jdbcTypeMap").getAnnotation(Column.class);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        MySqlTypeEnum mySqlTypeEnum = jdbcTypeMap.get(field.getType());

        // 指定了列类型
        if (!column.jdbcType().equals(MySqlTypeEnum.DEFAULT)) {
            mySqlTypeEnum = column.jdbcType();
        }

        // 枚举默认用 varchar
        if (mySqlTypeEnum == null && field.getType().isEnum()){
            mySqlTypeEnum = MySqlTypeEnum.VARCHAR;
        }

        if (mySqlTypeEnum == null) {
            throw new RuntimeException("不支持的列类型: " + field.getType().getName());
        }

        List<MySqlTypeEnum> oneLenList = Arrays.asList(MySqlTypeEnum.CHAR, MySqlTypeEnum.VARCHAR);
        List<MySqlTypeEnum> twoLenList = Collections.singletonList(MySqlTypeEnum.DECIMAL);

        String columnType = mySqlTypeEnum.name();
        if (oneLenList.contains(mySqlTypeEnum)) {
            columnType += String.format("(%d)", column.len());
        } else if (twoLenList.contains(mySqlTypeEnum)) {
            columnType += String.format("(%d,%d)", column.len(), column.scale());
        }
        return columnType.toLowerCase();

    }

    public static String getType(ColumnInfo columnInfo) {
        List<MySqlTypeEnum> oneLenList = Arrays.asList(MySqlTypeEnum.CHAR, MySqlTypeEnum.VARCHAR);
        List<MySqlTypeEnum> twoLenList = Collections.singletonList(MySqlTypeEnum.DECIMAL);

        MySqlTypeEnum mySqlTypeEnum = MySqlTypeEnum.of(columnInfo.getDATA_TYPE());
        String columnType = columnInfo.getDATA_TYPE();
        if (oneLenList.contains(mySqlTypeEnum)) {
            columnType += String.format("(%d)", columnInfo.getCHARACTER_MAXIMUM_LENGTH());
        } else if (twoLenList.contains(mySqlTypeEnum)) {
            columnType += String.format("(%d,%d)", columnInfo.getNUMERIC_PRECISION(), columnInfo.getNUMERIC_SCALE());
        }
        return columnType.toLowerCase();
    }

    /**
     * 判断列是否非空
     * 如果非空，返回 not null，如果空，则返回空字符串
     */
    public static String getNotNull(Field field) {
        Column column = field.getAnnotation(Column.class);
        return column == null || column.notNull() ? "not null" : "";
    }

    /**
     * 获取默认值
     */
    public static String getDefaultVal(Field field) {
        Column column = field.getAnnotation(Column.class);
        return column == null || column.defaultVal().isEmpty() ? "" : "default " + column.defaultVal();
    }

    /**
     * 获取字段注释
     */
    public static String getComment(Field field) {
        Column column = field.getAnnotation(Column.class);
        return column == null || column.comment().isEmpty() ? "" : String.format("comment '%s'", column.comment());
    }

    /**
     * 获取索引字段
     */
    public static String getIndexField(Field field) {
        Index index = field.getAnnotation(Index.class);
        StringBuilder sb = new StringBuilder();
        for (String f : index.fields()) {
            sb.append(f).append(",");
        }
        if (sb.length() > 0){
            sb.delete(sb.length()-1, sb.length());
        }

        String columnName = ColUtil.getName(field);
        return index.fields().length == 0 ? columnName : sb.toString();
    }

    /**
     * 打印建索引 sql
     */
    public static void printCreateIndexSql(String tableName, Field field, Index index) {
        String indexType = index.type().equals(IndexEnum.NORMAL) ? "" : "unique";
        String indexField = ColUtil.getIndexField(field);
        if (indexField == null){
            return;
        }

        System.err.printf("create %s index %s on %s (%s);%n", indexType, index.name(), tableName, indexField);
    }
}
