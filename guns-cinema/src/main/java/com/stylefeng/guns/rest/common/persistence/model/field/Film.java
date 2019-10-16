package com.stylefeng.guns.rest.common.persistence.model.field;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Film implements Serializable {
    private static final long serialVersionUID = 9085595113381569870L;
    private String actors;
    private String filmCats;
    private List<FilmField> filmFields;
    private int filmId;
    private String filmLength;
    private String filmName;
    private String filmType;
    private String imgAddress;
}
