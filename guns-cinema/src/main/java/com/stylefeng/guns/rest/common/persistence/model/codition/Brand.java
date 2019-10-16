package com.stylefeng.guns.rest.common.persistence.model.codition;

import lombok.Data;

import java.io.Serializable;

@Data
public class Brand implements Serializable {
    private static final long serialVersionUID = -6766737306827444472L;
    boolean active;
    int brandId;
    String brandName;
}
