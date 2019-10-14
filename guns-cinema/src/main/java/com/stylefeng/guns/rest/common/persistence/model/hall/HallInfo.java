package com.stylefeng.guns.rest.common.persistence.model.hall;

import lombok.Data;

import java.io.Serializable;

@Data
public class HallInfo implements Serializable {
    private static final long serialVersionUID = 4907618032549905940L;
    int discountPrice;
    int hallFieldId;
    String hallName;
    int price;
    String seatFile;
    String soldSeats;
}
