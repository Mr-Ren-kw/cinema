package com.stylefeng.guns.rest.common.persistence.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class FilmInfo implements Serializable {

    private static final long serialVersionUID = 6150598310452627477L;
    private Integer filmId;
    private Integer filmType;
    private String filmName;
    private String imgAddress;
    private String filmScore;

}
