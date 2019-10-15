package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/15 21:53
 */
@Data
public class FilmQueryByIdVO implements Serializable {

    private static final long serialVersionUID = 3211244652643335501L;
    private String filmName;
    private String filmEnName;
    private String imgAddress;
    private String score;
    private String scoreNum;
    private String totalBox;
    private String info01;
    private String info02;
    private String info03;
    private FilmDetail info4;
    private FilmImgVO imgVO;
    private String filmId;
}
