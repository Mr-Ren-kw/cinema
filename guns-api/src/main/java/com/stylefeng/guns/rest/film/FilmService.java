package com.stylefeng.guns.rest.film;

import com.stylefeng.guns.rest.film.vo.ConditionData;
import com.stylefeng.guns.rest.film.vo.FilmConditionVo;
import com.stylefeng.guns.rest.film.vo.IndexData;

public interface FilmService {
    //获取影院首页
    IndexData getIndex();

    ConditionData getFilmCondition(FilmConditionVo filmConditionVo);

    String getNameById(Integer filmId);

}
