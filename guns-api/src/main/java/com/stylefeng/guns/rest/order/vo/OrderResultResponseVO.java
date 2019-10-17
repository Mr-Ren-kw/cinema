package com.stylefeng.guns.rest.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/16 16:52
 */
@Data
public class OrderResultResponseVO implements Serializable {
    private static final long serialVersionUID = 6493550234204534316L;
    private String orderId;
    private String QRCodeAddress;
    private Integer orderStatus;
    private String orderMsg;
}
