package com.stylefeng.guns.rest.common.enums;

public enum PromoStatus {
    VALID(1),
    INVALID(0);

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    PromoStatus(int status) {
        this.status = status;
    }
}
