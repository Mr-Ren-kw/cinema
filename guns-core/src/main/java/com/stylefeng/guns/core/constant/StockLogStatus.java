package com.stylefeng.guns.core.constant;

public enum StockLogStatus {
    UNKNOWN(1, "初始化"),
    SUCCESS(2, "成功"),
    FAIL(3, "失败");
    private int index;
    private String message;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    StockLogStatus(int index, String message) {
        this.index = index;
        this.message = message;
    }
}
