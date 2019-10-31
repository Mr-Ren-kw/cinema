package com.stylefeng.guns.rest.promo.vo;

import java.io.Serializable;

public class PromoRespVO implements Serializable {
    private static final long serialVersionUID = -4215769012269383725L;

    private Integer status;
    private String msg;
    private Object data;

    public Integer getStatus() {
        return status;
    }

    public PromoRespVO() {
    }

    public PromoRespVO(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
