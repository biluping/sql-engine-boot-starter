package com.myboy.sql.util;

public class StrUtil {

    /**
     * 大小驼峰转下划线
     */
    public static String toUnderline(String str){
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toLowerCase(str.charAt(0)));

        for (int i = 1; i < str.length(); i++) {
            char at = str.charAt(i);
            if (Character.isUpperCase(at)){
                sb.append("_");
            }
            sb.append(Character.toLowerCase(at));
        }
        return sb.toString();
    }

}
