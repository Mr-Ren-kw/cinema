package com.stylefeng.guns.core.util;

import java.util.UUID;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/22 21:02
 */
public class UUIDUtil {

    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-","").substring(0,18);
    }
}
