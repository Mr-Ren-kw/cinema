package com.stylefeng.guns.rest.promo.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/19 15:32
 */
@Data
public class GetPromoByIdVO implements Serializable {

    private static final long serialVersionUID = 1771487228136499206L;
    private String cinemaAddress;
    private Integer cinemaId;
    private String cinemaName;
    private String description;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private String imgAddress;
    private Integer price;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    private Integer status;
    private Integer stock;
    private Integer uuid;
}
