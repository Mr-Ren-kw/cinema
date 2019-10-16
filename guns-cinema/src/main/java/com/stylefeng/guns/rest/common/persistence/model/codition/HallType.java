package com.stylefeng.guns.rest.common.persistence.model.codition;

import lombok.Data;

import java.io.Serializable;
@Data
public class HallType implements Serializable {
    private static final long serialVersionUID = 1443760396907716588L;
    private boolean active;
    private int halltypeId;
    private String halltypeName;

}
