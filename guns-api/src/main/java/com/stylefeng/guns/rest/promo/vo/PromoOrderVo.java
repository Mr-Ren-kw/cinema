package com.stylefeng.guns.rest.promo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PromoOrderVo implements Serializable {
    private static final long serialVersionUID = 7178915897754898438L;
    private int promoId;
    private int amount;
}
