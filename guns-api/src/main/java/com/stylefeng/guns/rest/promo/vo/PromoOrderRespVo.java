package com.stylefeng.guns.rest.promo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PromoOrderRespVo implements Serializable {
    private static final long serialVersionUID = -2216236767950962653L;
    private int status;
    private String msg;

    public static PromoOrderRespVo ok() {
        PromoOrderRespVo orderRespVo = new PromoOrderRespVo();
        orderRespVo.setStatus(0);
        orderRespVo.setMsg("下单成功！");
        return orderRespVo;
    }

    public static PromoOrderRespVo fail() {
        PromoOrderRespVo orderRespVo = new PromoOrderRespVo();
        orderRespVo.setStatus(1);
        orderRespVo.setMsg("下单失败！");
        return orderRespVo;
    }
}
