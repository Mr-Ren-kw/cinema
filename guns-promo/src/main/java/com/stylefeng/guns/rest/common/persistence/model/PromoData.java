package com.stylefeng.guns.rest.common.persistence.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PromoData implements Serializable {
    private static final long serialVersionUID = -8079395552725817486L;
    private String cinemaAddress;
    private Integer cinemaId;
    private String cinemaName;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss", timezone = "GMT+8")
    private Date endTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss", timezone = "GMT+8")
    private Date startTime;
    private String imgAddress;
    private Integer price;
    private Integer status;
    private Integer stock;
    private Integer uuid;
}
