package com.stylefeng.guns.rest.film;

import com.stylefeng.guns.rest.film.vo.FilmQueryByIdVO;
import com.stylefeng.guns.rest.film.vo.FilmsConditionVo;
import com.stylefeng.guns.rest.film.vo.FilmInfoVo;

import java.util.List;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/15 0:17
 */
public interface FilmService {

    /**
     * 影片查询接口
     * @param filmsConditionVo
     * @return FilmtVo数组
     */
    List<FilmInfoVo> getFilms(FilmsConditionVo filmsConditionVo);

    FilmQueryByIdVO queryFilmById(Integer filmId);
}
