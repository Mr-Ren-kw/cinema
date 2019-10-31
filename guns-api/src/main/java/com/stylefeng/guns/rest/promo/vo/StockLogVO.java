package com.stylefeng.guns.rest.promo.vo;

import java.io.Serializable;

public class StockLogVO implements Serializable {
    private static final long serialVersionUID = -7690469781695732806L;
    private Integer promoId;
    private Integer userId;
    private Integer amount;
    private String stockLogId;

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getStockLogId() {
        return stockLogId;
    }

    public void setStockLogId(String stockLogId) {
        this.stockLogId = stockLogId;
    }
}
