package com.stylefeng.guns.rest.common.enums;

public enum StockLogStatus {
    SUCCESS(2),
    FAIL(3),
    UNKNOWN(1);

    int index;

    StockLogStatus(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
