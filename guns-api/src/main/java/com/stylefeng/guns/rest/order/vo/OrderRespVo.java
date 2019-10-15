package com.stylefeng.guns.rest.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderRespVo<T> implements Serializable {
    private static final long serialVersionUID = -6903051375301962920L;
    private int status;
    private String msg;
    private T data;
}
