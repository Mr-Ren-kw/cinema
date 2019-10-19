package com.stylefeng.guns.rest.common.persistence.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PromoNewStock implements Serializable {
    private static final long serialVersionUID = -136773074455587127L;
    private Integer promoId;
    private Integer newStock;
}
