package com.stylefeng.guns.rest.common.persistence.model.codition;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CoditionData implements Serializable {
    private static final long serialVersionUID = 7903967434075954633L;
    List<Area> areaList;
    List<Brand> brandList;
    List<HallType> halltypeList;
}
