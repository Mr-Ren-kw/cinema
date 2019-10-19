package com.stylefeng.guns.rest.common.persistence.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class PromoOrderRespData implements Serializable {
    private static final long serialVersionUID = -3826311731886995809L;
    private String status;
    private String msg;

    public PromoOrderRespData fail() {
        this.setStatus("402");
        this.setMsg("下单失败，请重试");
        return this;
    }
}
