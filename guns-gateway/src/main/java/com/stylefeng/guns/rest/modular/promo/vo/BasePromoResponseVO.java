package com.stylefeng.guns.rest.modular.promo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/19 15:18
 */
@Data
public class BasePromoResponseVO<T> implements Serializable {

    private static final long serialVersionUID = -3220495708142287170L;
    private Integer status;
    private String msg;
    private T data;

}

