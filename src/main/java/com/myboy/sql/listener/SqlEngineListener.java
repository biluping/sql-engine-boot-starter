package com.myboy.sql.listener;

import com.myboy.sql.SqlProperties;
import com.myboy.sql.pojo.ColumnInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import com.myboy.sql.annotation.Column;
import com.myboy.sql.annotation.Index;
import com.myboy.sql.annotation.Table;
import com.myboy.sql.pojo.IndexInfoBO;
import com.myboy.sql.pojo.TableInfo;
import com.myboy.sql.util.ClassUtil;
import com.myboy.sql.util.ColUtil;
import com.myboy.sql.util.DbUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

/**
 * 框架入口，用于对比数据库字段与实体类字段，生成并打印对应的 sql
 */
@Order(Integer.MIN_VALUE)
public class SqlEngineListener implements ApplicationListener<ApplicationStartedEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SqlProperties sqlProperties;

    public SqlEngineListener(SqlProperties sqlProperties) {
        this.sqlProperties = sqlProperties;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("开始执行 sql 引擎");
        doProcess();
        log.info("sql 引擎 执行完成");
    }

    /**
     * 核心处理方法
     */
    private void doProcess() {
        String basePackage = sqlProperties.getBasePackage();
        if (basePackage == null || basePackage.isEmpty()) {
            log.warn("未配置 sql.basePackage");
            return;
        }

        Set<Class<?>> classSet = ClassUtil.scanClass(basePackage,
                clazz -> !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()));

        for (Class<?> clazz : classSet) {
            if (needCompare(clazz)) {
                Table table = clazz.getAnnotation(Table.class);
                TableInfo tableInfo = DbUtil.getTableInfo(table.name());
                if (tableInfo == null) {
                    // table 不存在，建表，建索引
                    createTable(clazz);
                    createIndex(clazz, table.name());
                } else {
                    // table 存在，比较
                    alterTable(clazz, tableInfo);
                    alterIndex(clazz, table.name());
                }
            }
        }

    }

    /**
     * 打印修改索引语句，其实就是先删除，在新建
     */
    private void alterIndex(Class<?> clazz, String tableName) {
        Map<String, IndexInfoBO> tableIndexMap = DbUtil.getTableIndex(tableName);

        while (!clazz.equals(Object.class)){
            for (Field field : clazz.getDeclaredFields()) {
                Index index = field.getAnnotation(Index.class);
                if (index == null){
                    continue;
                }
                if (index.name().isEmpty()){
                    log.warn("index name is empty, column:{}", ColUtil.getName(field));
                    continue;
                }

                IndexInfoBO indexInfoBO = tableIndexMap.get(index.name());
                if (indexInfoBO == null){
                    // 不存在索引，则打印新建索引
                    ColUtil.printCreateIndexSql(tableName, field, index);
                } else {
                    // 存在索引，则进行比较，如果有修改，则先删除索引，再新建索引
                    boolean needModifyIndex = !index.type().equals(indexInfoBO.getIndexEnum());
                    if (!ColUtil.getIndexField(field).equals(indexInfoBO.getFields())){
                        needModifyIndex = true;
                    }

                    if (needModifyIndex){
                        System.err.printf("alter table %s drop index %s;%n", tableName, index.name());
                        ColUtil.printCreateIndexSql(tableName, field, index);
                    }

                }

                tableIndexMap.remove(index.name());
            }
            clazz = clazz.getSuperclass();
        }

        // 剩下的索引都是多余的，直接删除
        tableIndexMap.forEach((indexName, bo) -> System.err.printf("alter table %s drop index %s;%n", tableName, indexName));
    }

    /**
     * 打印建索引语句
     */
    private void createIndex(Class<?> clazz, String tableName) {
        while (!clazz.equals(Object.class)){
            for (Field field : clazz.getDeclaredFields()) {
                Index index = field.getAnnotation(Index.class);
                if (index == null){
                    continue;
                }
                if (index.name().isEmpty()){
                    log.warn("index name is empty, column:{}", ColUtil.getName(field));
                    continue;
                }
                ColUtil.printCreateIndexSql(tableName, field, index);
            }
            clazz = clazz.getSuperclass();
        }

    }


    /**
     * 判断类是否需要和数据库比较
     */
    private boolean needCompare(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.getAnnotation(Table.class) != null;
    }

    /**
     * 建表语句逻辑
     */
    public void createTable(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);

        StringBuilder sb = new StringBuilder();
        sb.append("create table ").append(table.name()).append(" (\n");
        while (!clazz.equals(Object.class)){
            for (Field field : clazz.getDeclaredFields()) {
                String columnSql = ColUtil.getColumnSql(field, true);
                sb.append("  ").append(columnSql).append(",\n");
            }
            clazz = clazz.getSuperclass();
        }

        sb.delete(sb.length() - 2, sb.length());
        sb.append("\n)");

        if (!table.comment().isEmpty()) {
            sb.append(String.format(" comment '%s'", table.comment()));
        }

        sb.append(";");

        System.err.println(sb);
    }


    /**
     * 改表语句逻辑
     */
    private void alterTable(Class<?> clazz, TableInfo tableInfo) {
        Table table = clazz.getAnnotation(Table.class);
        // 改表注释 sql
        if (!table.comment().equals(tableInfo.getTABLE_COMMENT())) {
            System.err.printf("alter table %s comment = '%s';%n", table.name(), table.comment());
        }

        Map<String, ColumnInfo> columnInfoMap = DbUtil.getColumnInfo(table.name());

        while (!clazz.equals(Object.class)){
            for (Field field : clazz.getDeclaredFields()) {
                String columnName = ColUtil.getName(field);
                ColumnInfo columnInfo = columnInfoMap.get(columnName);
                if (columnInfo == null) {
                    // 加字段
                    System.err.printf("alter table %s add column %s;%n", table.name(), ColUtil.getColumnSql(field, true));
                    continue;
                }

                // 是否需要修改字段，类型比较
                boolean needModifyTable = !ColUtil.getType(field).equals(ColUtil.getType(columnInfo));

                // not null 比较，使用异或运行，当这俩不相同，代码需要修改
                if (ColUtil.getNotNull(field).isEmpty() ^ "YES".equals(columnInfo.getIS_NULLABLE())) {
                    needModifyTable = true;
                }

                // default 比较
                Column column = field.getAnnotation(Column.class);
                String defaultVal = column != null ? column.defaultVal() : "";
                // 去除前后的单引号，否则比较永远都是false
                defaultVal = defaultVal.replaceAll("^'|'$", "");
                String dbColDefault = columnInfo.getCOLUMN_DEFAULT() != null ? columnInfo.getCOLUMN_DEFAULT() : "";
                if (columnInfo.getDATA_TYPE().equals("bit") && !dbColDefault.isEmpty()){
                    // bit 类型的默认值，查出来shi  b'1'、b'0' 这种形式，需要给他去一下外面一层
                    dbColDefault = dbColDefault.substring(1).replaceAll("^'|'$", "");
                }
                if (!defaultVal.equals(dbColDefault)) {
                    needModifyTable = true;
                }

                // comment 比较
                String comment = column != null ? column.comment() : "";
                String dbComment = columnInfo.getCOLUMN_COMMENT() != null ? columnInfo.getCOLUMN_COMMENT() : "";
                if (!comment.equals(dbComment)) {
                    needModifyTable = true;
                }

                // primary key 比较
                boolean dbHasKey = columnInfo.getCOLUMN_KEY() != null && columnInfo.getCOLUMN_KEY().contains("PRI");
                boolean columnHasKey = column != null && column.isKey();
                if (dbHasKey ^ columnHasKey){
                    needModifyTable = true;
                }

                // 自动递增判断
                boolean dbAutoIncr = columnInfo.getEXTRA()!=null && columnInfo.getEXTRA().contains("auto_increment");
                boolean columnAutoIncr = column != null && column.autoIncrement();
                if (dbAutoIncr ^ columnAutoIncr){
                    needModifyTable = true;
                }

            /*
            情况 1:  实体类标注是主键
                        db 列是主键：打印语句不包含 primary key
                        db 列不是主键：打印语句包含 primary key
                     实体类标注不是主键
                        db 列是主键：先打印语句不包含 primary key, 再打印删除主键 sql
                        db 列不是主键：打印语句不包含 primary key

                // 归纳之后，伪代码如下

                    if(实体类标注是主键 && db 列不是主键){
                        打印语句包含 primary key
                    } else {
                        打印语句不包含 primary key
                        if(实体类标注不是主键 && db 列是主键) {
                            再打印删除主键 sql
                        }
                    }
            */

                if (needModifyTable){
                    System.err.printf("alter table %s modify column %s;%n", table.name(), ColUtil.getColumnSql(field, columnHasKey && !dbHasKey));
                    if (!columnHasKey && dbHasKey) {
                        System.err.printf("alter table %s drop primary key;%n", table.name());
                    }
                }

                columnInfoMap.remove(columnName);

            }
            clazz = clazz.getSuperclass();
        }

        // 剩下没有遍历到的，打印删除语句
        columnInfoMap.forEach((columnName, columnInfo) -> System.err.printf("alter table %s drop column %s;%n", table.name(), columnName));

    }

}
