package com.stylefeng.guns.rest.common.persistence.model.codition;

import lombok.Data;

import java.io.Serializable;

@Data
public class Area implements Serializable {
    private static final long serialVersionUID = 8256779661200908888L;
    private boolean active;
    private int areaId;
    private String areaName;

}
