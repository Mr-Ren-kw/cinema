package com.stylefeng.guns.rest.modular.promo.util;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class UuidUtil {
    private UuidUtil(){}

    /**
     * 30位的uuid
     * @return uuid
     */
    public static String getUUID() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = sdf.format(new Date());
        Random random = new Random();
//        int i = (int)(1000000 * random.nextDouble()) + 1;

        String s = UUID.randomUUID().toString().replaceAll("-", "");
        return now + s.substring(s.length() - 16);
    }

}
