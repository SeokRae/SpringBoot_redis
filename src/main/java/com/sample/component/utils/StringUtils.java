package com.sample.component.utils;

public class StringUtils {
    public static String getOrDefault(String value, String defaultVal) {
        return value != null ? value : defaultVal;
    }
}
