package com.stylefeng.guns.rest.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BuyTicketsVo implements Serializable {
    private static final long serialVersionUID = 451326754696934865L;
    private int fieldId;
    private String soldSeats;
    private String seatsName;
}
