package com.stylefeng.guns.rest.common.persistence.model.field;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FieldData implements Serializable {
    private static final long serialVersionUID = 561243558752793197L;
    private CinemaInfo cinemaInfo;
    private List<Film> filmList;
}
