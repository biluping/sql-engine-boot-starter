package com.myboy.sql.pojo;

import com.myboy.sql.constant.IndexEnum;
import lombok.Data;

@Data
public class IndexInfoBO {

    private String indexName;

    private String fields;

    private IndexEnum indexEnum;

}
