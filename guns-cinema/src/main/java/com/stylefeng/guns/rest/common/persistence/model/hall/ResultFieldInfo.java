package com.stylefeng.guns.rest.common.persistence.model.hall;

import com.stylefeng.guns.rest.common.persistence.model.field.CinemaInfo;
import com.stylefeng.guns.rest.common.persistence.model.field.Film;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResultFieldInfo implements Serializable {
    private static final long serialVersionUID = 407852188273897981L;
    CinemaInfo cinemaInfo;
    Film filmInfo;
    HallInfo hallInfo;
}
