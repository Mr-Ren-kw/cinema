package com.stylefeng.guns.core.util;

import java.util.UUID;

public class UuidUtil {
    public static String getUuidOf16() {
        String s = UUID.randomUUID().toString().replaceAll("-", "");
        return s.substring(0, 16);
    }
}
