package com.myboy.sql.pojo;

public class IndexInfo {

    private String INDEX_NAME;

    private String COLUMN_NAME;

    private Integer SEQ_IN_INDEX;

    private Integer SUB_PART;

    private Integer NON_UNIQUE;

    public Integer getNON_UNIQUE() {
        return NON_UNIQUE;
    }

    public void setNON_UNIQUE(Integer NON_UNIQUE) {
        this.NON_UNIQUE = NON_UNIQUE;
    }

    public String getINDEX_NAME() {
        return INDEX_NAME;
    }

    public void setINDEX_NAME(String INDEX_NAME) {
        this.INDEX_NAME = INDEX_NAME;
    }

    public String getCOLUMN_NAME() {
        return COLUMN_NAME;
    }

    public void setCOLUMN_NAME(String COLUMN_NAME) {
        this.COLUMN_NAME = COLUMN_NAME;
    }

    public Integer getSEQ_IN_INDEX() {
        return SEQ_IN_INDEX;
    }

    public void setSEQ_IN_INDEX(Integer SEQ_IN_INDEX) {
        this.SEQ_IN_INDEX = SEQ_IN_INDEX;
    }

    public Integer getSUB_PART() {
        return SUB_PART;
    }

    public void setSUB_PART(Integer SUB_PART) {
        this.SUB_PART = SUB_PART;
    }
}
