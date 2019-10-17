package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.film.FilmService;
import com.stylefeng.guns.rest.film.vo.FilmInfoVo;
import com.stylefeng.guns.rest.film.vo.FilmQueryByIdVO;
import com.stylefeng.guns.rest.modular.film.vo.BaseFilmResponseVo;
import com.stylefeng.guns.rest.film.vo.FilmsConditionVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/15 0:12
 */
import com.stylefeng.guns.rest.film.vo.*;
import com.stylefeng.guns.rest.modular.film.vo.BaseFilmResponseVo;
import com.stylefeng.guns.rest.film.vo.FilmConditionVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/film")
public class FilmController {


    @Reference(interfaceClass = FilmService.class,check = false)
    FilmService filmService;


    @GetMapping("/getFilms")
    public BaseFilmResponseVo hello(FilmsConditionVo filmsConditionVo) {

        List<FilmInfoVo> films = filmService.getFilms(filmsConditionVo);

        BaseFilmResponseVo baseFilmResponseVo = new BaseFilmResponseVo();
        baseFilmResponseVo.setStatus(0);
        baseFilmResponseVo.setImgPre("http://img.meetingshop.cn/");
        baseFilmResponseVo.setNowPage(filmsConditionVo.getNowPage());
        int size = films.size();
        Integer pageSize = filmsConditionVo.getPageSize();
        int totalPage = (int) Math.ceil(size/pageSize);
        baseFilmResponseVo.setTotalPage(totalPage);
        baseFilmResponseVo.setData(films);
        return baseFilmResponseVo;
    }

    @GetMapping("/films/{filmId}")
    public BaseFilmResponseVo queryFilmById(@PathVariable("filmId") Integer filmId) {
        FilmQueryByIdVO filmQueryByIdVO = filmService.queryFilmById(filmId);
        BaseFilmResponseVo baseFilmResponseVo = new BaseFilmResponseVo();
        baseFilmResponseVo.setStatus(0);
        baseFilmResponseVo.setImgPre("http://img.meetingshop.cn/");
        baseFilmResponseVo.setData(filmQueryByIdVO);
        return baseFilmResponseVo;
    }
    @RequestMapping("/getIndex")
    public BaseFilmResponseVo getIndex() {
        BaseFilmResponseVo<IndexData> response = new BaseFilmResponseVo<>();
        IndexData indexData = filmService.getIndex();
        response.setData(indexData);
        response.setImgPre("http://img.meetingshop.cn/");
        response.setStatus(0);
        return response;
    }

    @RequestMapping("/getConditionList")
    public BaseFilmResponseVo getConditionList(FilmConditionVo filmConditionVo) {
        BaseFilmResponseVo<ConditionData> response = new BaseFilmResponseVo<>();
        ConditionData conditionData = filmService.getFilmCondition(filmConditionVo);
        response.setData(conditionData);
        response.setStatus(0);
        return response;
    }
}
