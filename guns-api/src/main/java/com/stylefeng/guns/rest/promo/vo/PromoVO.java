package com.stylefeng.guns.rest.promo.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PromoVO implements Serializable {
    private static final long serialVersionUID = -7370597101686451266L;
    private String cinemaAddress;
    private Integer cinemaId;
    private String cinemaName;
    private String description;
    private Date endTime;
    private String imgAddress;
    private Integer price;
    private Date startTime;
    private Integer status; // 1为有效， 0为无效，根据开始时间和结束时间筛选正在进行的活动
    private Integer stock;
    private Integer uuid;

    public PromoVO() {
    }

    public PromoVO(String cinemaAddress, Integer cinemaId, String cinemaName, String description, Date endTime, String imgAddress, Integer price, Date startTime, Integer status, Integer stock, Integer uuid) {
        this.cinemaAddress = cinemaAddress;
        this.cinemaId = cinemaId;
        this.cinemaName = cinemaName;
        this.description = description;
        this.endTime = endTime;
        this.imgAddress = imgAddress;
        this.price = price;
        this.startTime = startTime;
        this.status = status;
        this.stock = stock;
        this.uuid = uuid;
    }
}
