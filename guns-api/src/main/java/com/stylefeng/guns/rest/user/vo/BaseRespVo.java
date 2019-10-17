package com.stylefeng.guns.rest.user.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class BaseRespVo implements Serializable {
    private static final long serialVersionUID = -7576875004902320078L;

    private Object data;
    private String imgPre;
    private String msg;
    private String nowPage;
    private Integer status;
    private String totalPage;

    public BaseRespVo(String msg, Integer status) {
        this.msg = msg;
        this.status = status;
    }

    public BaseRespVo(Object data, Integer status) {
        this.data = data;
        this.status = status;
    }
}
