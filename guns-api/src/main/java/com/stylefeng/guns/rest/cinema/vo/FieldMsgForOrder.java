package com.stylefeng.guns.rest.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FieldMsgForOrder implements Serializable {
    private static final long serialVersionUID = -3073769550247439805L;
    private int cinemaId;
    private int filmId;
    private double price;
}
