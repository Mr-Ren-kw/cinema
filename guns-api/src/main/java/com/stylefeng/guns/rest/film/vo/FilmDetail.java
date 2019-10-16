package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/15 21:57
 */
@Data
public class FilmDetail implements Serializable {

    private static final long serialVersionUID = -1187364132774938872L;
    private String biography;   // 电影简介
    private FilmActors actors;


}
