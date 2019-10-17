package com.stylefeng.guns.rest.common.persistence.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Source implements Serializable {

    private static final long serialVersionUID = 4578976714670463785L;
    private String sourceId;
    private boolean active;
    private String sourceName;

}
