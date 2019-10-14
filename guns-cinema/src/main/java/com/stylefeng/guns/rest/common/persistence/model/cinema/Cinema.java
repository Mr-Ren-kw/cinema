package com.stylefeng.guns.rest.common.persistence.model.cinema;

import lombok.Data;

import java.io.Serializable;
@Data
public class Cinema implements Serializable {
    private static final long serialVersionUID = -4193803328719898939L;
   String cinemaAddress;
   String cinemaName;
   int minimumPrice;
   int uuid;
}
