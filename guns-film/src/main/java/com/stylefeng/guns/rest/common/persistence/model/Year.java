package com.stylefeng.guns.rest.common.persistence.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class Year implements Serializable {

    private static final long serialVersionUID = 5760667629412776142L;
    private boolean active;
    private String yearName;
    private String yearId;

}
