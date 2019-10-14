package com.stylefeng.guns.rest.cinema.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class CinemaVo implements Serializable {
    private static final long serialVersionUID = 815210865497311922L;
    private Integer brandId;
    private Integer hallType;
    private Integer areaId;
    private Integer pageSize;
    private Integer nowPage;
}
