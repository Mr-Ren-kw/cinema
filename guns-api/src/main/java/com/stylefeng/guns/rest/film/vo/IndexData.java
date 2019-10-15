package com.stylefeng.guns.rest.film.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class IndexData<T> implements Serializable {

    private static final long serialVersionUID = 2905592131972051337L;
    List<T> banners;
    List<T> boxRanking;
    List<T> expectRanking;
    FilmPage hotFilms;
    FilmPage soonFilms;
    List<T> top100;

}
