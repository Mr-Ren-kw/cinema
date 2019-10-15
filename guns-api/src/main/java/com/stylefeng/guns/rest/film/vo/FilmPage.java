package com.stylefeng.guns.rest.film.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class FilmPage<T> implements Serializable {

    private static final long serialVersionUID = 3601134583430704292L;
    List<T> filmInfo;
    Integer filmNum;

}
