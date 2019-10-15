package com.stylefeng.guns.rest.film;

import com.stylefeng.guns.rest.film.vo.ConditionData;
import com.stylefeng.guns.rest.film.vo.FilmConditionVo;
import com.stylefeng.guns.rest.film.vo.IndexData;

public interface FilmService {
    IndexData getIndex();

    ConditionData getFilmCondition(FilmConditionVo filmConditionVo);
}
