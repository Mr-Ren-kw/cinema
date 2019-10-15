package com.stylefeng.guns.rest.common.persistence.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class Cat implements Serializable {

    private static final long serialVersionUID = -6644295823162643063L;
    private String catId;
    private String catName;
    private boolean active;

}
