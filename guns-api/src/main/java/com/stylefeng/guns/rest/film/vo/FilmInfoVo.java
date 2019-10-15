package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/15 14:57
 */
@Data
public class FilmInfoVo implements Serializable {


    private static final long serialVersionUID = -7993853361868903828L;
    /**
     * filmId : 001
     * filmType : 1
     * filmName : 我不是药神
     * imgAddress : img/film/001.jpg
     * filmScore : 8.3
     */
    private Integer filmId;
    private Integer filmType;
    private String filmName;
    private String imgAddress;
    private String filmScore;


}
