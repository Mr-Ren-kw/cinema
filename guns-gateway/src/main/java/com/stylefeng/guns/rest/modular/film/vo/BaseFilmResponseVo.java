package com.stylefeng.guns.rest.modular.film.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class BaseFilmResponseVo<T> implements Serializable {

    private static final long serialVersionUID = -6711520992107891863L;
    private T data;
    private String imgPre;
    private String msg;
    private Integer nowPage;
    private Integer status;
    private Integer totalPage;

}
