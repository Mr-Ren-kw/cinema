package com.stylefeng.guns.rest.common.persistence.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class FilmInfo implements Serializable {

    private static final long serialVersionUID = 6150598310452627477L;
    private Integer filmId;
    private Integer filmType;
    private String filmName;
    private String imgAddress;
    private String filmScore;
    private String score;
    private Integer expectNum;
    private Integer boxNum;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date showTime;
}
