package com.myboy.sql;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sql")
public class SqlProperties {

    /**
     * 扫描的实体类包路径
     */
    private String basePackage;

}
