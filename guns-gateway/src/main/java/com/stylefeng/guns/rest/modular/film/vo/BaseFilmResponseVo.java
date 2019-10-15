package com.stylefeng.guns.rest.modular.film.vo;

import lombok.Data;

@Data
public class BaseFilmResponseVo<T> {

    private T data;
    private String imgPre;
    private Integer status;

}
