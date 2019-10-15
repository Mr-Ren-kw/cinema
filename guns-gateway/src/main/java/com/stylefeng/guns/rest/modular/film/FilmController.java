package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.film.FilmService;
import com.stylefeng.guns.rest.film.vo.*;
import com.stylefeng.guns.rest.modular.film.vo.BaseFilmResponseVo;
import com.stylefeng.guns.rest.film.vo.FilmConditionVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/film")
public class FilmController {

    @Reference(interfaceClass = FilmService.class)
    private FilmService filmService;

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
