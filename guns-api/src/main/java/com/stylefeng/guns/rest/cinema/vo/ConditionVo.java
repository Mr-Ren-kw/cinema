package com.stylefeng.guns.rest.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ConditionVo implements Serializable {
    private static final long serialVersionUID = 8808708859298306172L;
    private int brandId;
    private int hallType;
    private int areaId;
}
