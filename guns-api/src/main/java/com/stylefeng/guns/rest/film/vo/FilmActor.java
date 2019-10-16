package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/15 22:10
 */
@Data
public class FilmActor implements Serializable {

    private static final long serialVersionUID = 5519662421908472716L;
    private String imgAddress;
    private String directorName;
    private String roleName;
}
