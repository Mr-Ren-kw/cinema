package com.stylefeng.guns.rest.common.persistence.model.field;

import lombok.Data;

import java.io.Serializable;

@Data
public class CinemaInfo implements Serializable {
    private static final long serialVersionUID = 6018302026341416679L;
    private String cinemaAdress;
    private int cinemaId;
    private String cinemaName;
    private String cinemaPhone;
    private String imgUrl;
}
