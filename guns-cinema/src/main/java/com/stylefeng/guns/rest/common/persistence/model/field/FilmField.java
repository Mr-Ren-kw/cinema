package com.stylefeng.guns.rest.common.persistence.model.field;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmField implements Serializable {
    private static final long serialVersionUID = 3126024765533088008L;
    private String beginTime;
    private String endTime;
    private int fieldId;
    private String hallName;
    private String language;
    private String price;
}
