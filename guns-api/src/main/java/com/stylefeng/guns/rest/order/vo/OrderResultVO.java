package com.stylefeng.guns.rest.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/16 16:15
 */
@Data
public class OrderResultVO implements Serializable {

    private static final long serialVersionUID = 5256735162657079647L;
    private Integer orderId;
    private Integer tryNums;
}
