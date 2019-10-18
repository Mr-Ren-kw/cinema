package com.stylefeng.guns.rest.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CinemaMsgForPromo implements Serializable {
    private static final long serialVersionUID = 950325653038685436L;
    private String cinemaAddress;
    private String cinemaName;
    private String imgAddress;
}
