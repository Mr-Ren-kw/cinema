package com.stylefeng.guns.rest.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderRespVo<T> implements Serializable {
    private static final long serialVersionUID = -6903051375301962920L;
    private int status;
    private String msg;
    private T data;
    private String imgPre;

    public static OrderRespVo fail(OrderRespVo orderRespVo) {
        orderRespVo.setStatus(1);
        orderRespVo.setMsg("该订单不存在");
        return orderRespVo;
    }

    public OrderRespVo(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public OrderRespVo() {
    }
}
