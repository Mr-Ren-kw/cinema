package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/15 22:11
 */
@Data
public class FilmActors implements Serializable {

    private static final long serialVersionUID = -5014787263896780611L;
    private FilmActor director;
    private List<FilmActor> actors;
}
