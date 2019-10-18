package com.stylefeng.guns.rest.promo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PromoRespVo<T> implements Serializable {
    private static final long serialVersionUID = 6458671122025032476L;
    private int status;
    private String msg;
    private T data;

    public PromoRespVo fail(int status,String msg) {
        this.setStatus(status);
        this.setMsg(msg);
        return this;
    }

    public PromoRespVo<T> ok(T data) {
        this.setData(data);
        return this;
    }
}
