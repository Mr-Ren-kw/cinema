package com.stylefeng.guns.rest.promo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PromoStockRespVo implements Serializable {
    private static final long serialVersionUID = 943507180880235612L;
    private int status;
    private String msg;

    public static PromoStockRespVo success(String msg) {
        PromoStockRespVo stockRespVo = new PromoStockRespVo();
        stockRespVo.setStatus(0);
        stockRespVo.setMsg(msg);
        return stockRespVo;
    }
    public static PromoStockRespVo fail(String msg){
        PromoStockRespVo stockRespVo = new PromoStockRespVo();
        stockRespVo.setStatus(1);
        stockRespVo.setMsg(msg);
        return stockRespVo;
    }
}
