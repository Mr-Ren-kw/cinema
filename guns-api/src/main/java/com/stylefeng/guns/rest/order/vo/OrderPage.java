package com.stylefeng.guns.rest.order.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class OrderPage implements Serializable {
    private static final long serialVersionUID = -4258854818291068634L;
    private Integer nowPage;
    private Integer pageSize;
}
