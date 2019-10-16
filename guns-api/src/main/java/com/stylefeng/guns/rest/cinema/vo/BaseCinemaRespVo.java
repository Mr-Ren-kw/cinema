package com.stylefeng.guns.rest.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseCinemaRespVo<T> implements Serializable {
    private static final long serialVersionUID = 8121302763305628525L;
    private T data;
    private String imgPre;
    private String msg;
    private int nowPage;
    private int status;
    private int totalPage;

    public static BaseCinemaRespVo ok(Object data){
        BaseCinemaRespVo baseRespVo = new BaseCinemaRespVo();
        baseRespVo.setData(data);
        baseRespVo.setStatus(0);
        return baseRespVo;
    }

}
