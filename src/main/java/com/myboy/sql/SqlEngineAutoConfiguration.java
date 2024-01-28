package com.myboy.sql;

import com.myboy.sql.listener.SqlEngineListener;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.myboy.sql.util.DbUtil;

@Configuration
@EnableConfigurationProperties(SqlProperties.class)
public class SqlEngineAutoConfiguration {

    /**
     * 程序入口
     */
    @Bean
    public SqlEngineListener sqlGenerateListener(SqlProperties sqlProperties){
        return new SqlEngineListener(sqlProperties);
    }

    /**
     * 数据库操作工具类
     */
    @Bean
    public DbUtil dbUtil(){
        return new DbUtil();
    }
}
