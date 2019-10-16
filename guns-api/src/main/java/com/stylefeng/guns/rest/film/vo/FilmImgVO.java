package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/15 21:59
 */
@Data
public class FilmImgVO implements Serializable {

    private static final long serialVersionUID = 3284830218412985273L;
    private String mainImg;
    private String img01;
    private String img02;
    private String img03;
    private String img04;
}
