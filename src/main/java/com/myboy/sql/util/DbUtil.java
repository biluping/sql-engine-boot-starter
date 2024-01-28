package com.myboy.sql.util;

import com.myboy.sql.constant.IndexEnum;
import com.myboy.sql.pojo.ColumnInfo;
import com.myboy.sql.pojo.IndexInfo;
import com.myboy.sql.pojo.IndexInfoBO;
import com.myboy.sql.pojo.TableInfo;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 数据库操作工具类
 */
public class DbUtil implements ApplicationContextAware {

    private static JdbcTemplate jdbcTemplate;

    /**
     * 根据表名，获取表的信息
     */
    public static TableInfo getTableInfo(String tableName) {
        String getTableInfoSql = "select * from information_schema.tables where table_name = ? and table_schema = (select database())";
        List<TableInfo> infoList = jdbcTemplate.query(getTableInfoSql, new BeanPropertyRowMapper<>(TableInfo.class), tableName);
        return infoList.isEmpty() ? null : infoList.get(0);
    }

    public static Map<String, ColumnInfo> getColumnInfo(String tableName) {
        String getColumnInfoSql = "select * from information_schema.columns where table_name = ? and table_schema = (select database())";
        List<ColumnInfo> list = jdbcTemplate.query(getColumnInfoSql, new BeanPropertyRowMapper<>(ColumnInfo.class), tableName);
        return list.stream().collect(Collectors.toMap(ColumnInfo::getCOLUMN_NAME, Function.identity()));
    }

    /**
     * 获取索引字段信息
     * {"idx_user_age", "user(2),age"}
     */
    public static Map<String/*index name*/, IndexInfoBO> getTableIndex(String tableName) {
        String sql = "select * from information_schema.statistics WHERE table_name = ? and  table_schema = (select database()) and index_name != 'PRIMARY'";
        List<IndexInfo> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(IndexInfo.class), tableName);
        Map<String, List<IndexInfo>> groupByMap = list.stream().collect(Collectors.groupingBy(IndexInfo::getINDEX_NAME));

        HashMap<String, IndexInfoBO> map = new HashMap<>();
        for (Map.Entry<String, List<IndexInfo>> entry : groupByMap.entrySet()) {
            String fields = entry.getValue()
                    .stream()
                    .sorted(Comparator.comparing(IndexInfo::getSEQ_IN_INDEX))
                    .map(e -> {
                        String field = e.getCOLUMN_NAME();
                        if (e.getSUB_PART() != null) {
                            field += String.format("(%d)", e.getSUB_PART());
                        }
                        return field;
                    }).collect(Collectors.joining(","));

            IndexInfoBO indexInfoBO = new IndexInfoBO();
            indexInfoBO.setIndexName(entry.getKey());
            indexInfoBO.setFields(fields);
            indexInfoBO.setIndexEnum(entry.getValue().get(0).getNON_UNIQUE() == 1 ? IndexEnum.NORMAL : IndexEnum.UNIQUE);

            map.put(entry.getKey(), indexInfoBO);
        }
        return map;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
    }
}
