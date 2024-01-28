package com.myboy.sql.pojo;

import lombok.Data;

@Data
public class IndexInfo {

    private String INDEX_NAME;

    private String COLUMN_NAME;

    private Integer SEQ_IN_INDEX;

    private Integer SUB_PART;

    private Integer NON_UNIQUE;
}
