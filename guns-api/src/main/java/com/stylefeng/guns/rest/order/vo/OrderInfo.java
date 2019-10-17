package com.stylefeng.guns.rest.order.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = 5185092189056857398L;
    private String orderId;
    private String filmName;
    private String fieldTime;
    private String cinemaName;
    private String seatsName;
    private String orderPrice;
    private String orderStatus;
}
