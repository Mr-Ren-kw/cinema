package com.stylefeng.guns.rest.film.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class ConditionData<T,V,W> implements Serializable {

    private static final long serialVersionUID = 1227340045330232367L;
    List<T> catInfo;
    List<V> sourceInfo;
    List<W> yearInfo;
}
