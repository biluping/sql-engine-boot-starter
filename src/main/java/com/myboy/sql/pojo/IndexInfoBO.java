package com.myboy.sql.pojo;

import com.myboy.sql.constant.IndexEnum;

public class IndexInfoBO {

    private String indexName;

    private String fields;

    private IndexEnum indexEnum;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public IndexEnum getIndexEnum() {
        return indexEnum;
    }

    public void setIndexEnum(IndexEnum indexEnum) {
        this.indexEnum = indexEnum;
    }
}
