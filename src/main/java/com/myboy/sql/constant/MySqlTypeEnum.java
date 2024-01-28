package com.myboy.sql.constant;


import java.util.Arrays;

public enum MySqlTypeEnum {

	DEFAULT,
	INT,
	VARCHAR,
	BINARY,
	CHAR,
	BIGINT,
	BIT,
	TINYINT,
	SMALLINT,
	MEDIUMINT,
	DECIMAL,
	DOUBLE,
	TEXT,
	MEDIUMTEXT,
	LONGTEXT,
	DATETIME,
	TIMESTAMP,
	DATE,
	TIME,
	FLOAT,
	YEAR,
	BLOB,
	LONGBLOB,
	MEDIUMBLOB,
	TINYTEXT,
	TINYBLOB,
	JSON;

	public static MySqlTypeEnum of(String type){
		return Arrays.stream(values()).filter(e -> e.name().equalsIgnoreCase(type)).findFirst().orElse(null);
	}
}
