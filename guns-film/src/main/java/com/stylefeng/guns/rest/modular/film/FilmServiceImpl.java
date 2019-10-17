package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.film.FilmService;
import com.stylefeng.guns.rest.film.vo.ConditionData;
import com.stylefeng.guns.rest.film.vo.FilmConditionVo;
import com.stylefeng.guns.rest.film.vo.FilmPage;
import com.stylefeng.guns.rest.film.vo.IndexData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = FilmService.class)
@Component
public class FilmServiceImpl implements FilmService{

    @Autowired
    MtimeBannerTMapper mtimeBannerTMapper;
    @Autowired
    MtimeFilmTMapper mtimeFilmTMapper;
    @Autowired
    MtimeCatDictTMapper mtimeCatDictTMapper;
    @Autowired
    MtimeSourceDictTMapper mtimeSourceDictTMapper;
    @Autowired
    MtimeYearDictTMapper mtimeYearDictTMapper;

    private static final Integer HOT_FILM = 8;
    private static final Integer SOON_FILM = 8;
    private static final Integer BOX_RANK = 9;
    private static final Integer EXCEPT_RANK = 7;
    private static final Integer TOP_100 = 9;

    @Override
    public IndexData getIndex() {
        IndexData data = new IndexData();
        List<Banner> banners = mtimeBannerTMapper.queryList();
        Page<MtimeFilmT> page = new Page<>(1, BOX_RANK);
        EntityWrapper<MtimeFilmT> filmWrapper = new EntityWrapper<>();
        filmWrapper.orderBy("film_box_office", false);
        List<MtimeFilmT> expectList = mtimeFilmTMapper.selectPage(page, filmWrapper);
        if(expectList.size() == 0) {
            return new IndexData();
        }
        List<FilmInfo> boxRanking = typeConverse(expectList);
        page.setSize(EXCEPT_RANK);
        filmWrapper.orderBy("film_preSaleNum", false);
        expectList = mtimeFilmTMapper.selectPage(page, filmWrapper);
        List<FilmInfo> expectRanking = typeConverse(expectList);
        filmWrapper.orderBy("film_score", false);
        page.setSize(TOP_100);
        expectList = mtimeFilmTMapper.selectPage(page, filmWrapper);
        List<FilmInfo> top100 = typeConverse(expectList);
        page.setSize(HOT_FILM);
        filmWrapper.eq("film_status", 1);
        List<MtimeFilmT> hotFilmT = mtimeFilmTMapper.selectPage(page, filmWrapper);
        List<FilmInfo> hotFilms = typeConverse(expectList);
        page.setSize(SOON_FILM);
        filmWrapper.eq("film_status", 2);
        List<MtimeFilmT> soonFilmT = mtimeFilmTMapper.selectPage(page, filmWrapper);
        List<FilmInfo> soonFilms = typeConverse(expectList);
        FilmPage hotFilmPage = new FilmPage<FilmInfo>();
        hotFilmPage.setFilmNum(hotFilms.size());
        hotFilmPage.setFilmInfo(hotFilms);
        FilmPage soonFilmPage = new FilmPage<FilmInfo>();
        soonFilmPage.setFilmNum(soonFilms.size());
        soonFilmPage.setFilmInfo(soonFilms);
        data.setBanners(banners);
        data.setBoxRanking(boxRanking);
        data.setExpectRanking(expectRanking);
        data.setHotFilms(hotFilmPage);
        data.setSoonFilms(soonFilmPage);
        data.setTop100(top100);
        return data;
    }

    private List<FilmInfo> typeConverse(List<MtimeFilmT> mtimeFilmTs) {
        List<FilmInfo> filmInfos = new ArrayList<>();
        for (MtimeFilmT mtimeFilmT : mtimeFilmTs) {
            FilmInfo filmInfo = new FilmInfo();
            filmInfo.setFilmId(mtimeFilmT.getUuid());
            filmInfo.setFilmName(mtimeFilmT.getFilmName());
            filmInfo.setFilmScore(mtimeFilmT.getFilmScore());
            filmInfo.setFilmType(mtimeFilmT.getFilmType());
            filmInfo.setImgAddress(mtimeFilmT.getImgAddress());
            filmInfo.setExpectNum(mtimeFilmT.getFilmPresalenum());
            filmInfo.setScore(mtimeFilmT.getFilmScore());
            filmInfo.setBoxNum(mtimeFilmT.getFilmBoxOffice());
            filmInfo.setShowTime(mtimeFilmT.getFilmTime());
            filmInfos.add(filmInfo);
        }
        return filmInfos;
    }

    @Override
    public ConditionData getFilmCondition(FilmConditionVo filmConditionVo) {
        ConditionData<Cat, Source, Year> data = new ConditionData<>();
        List<MtimeCatDictT> cats = mtimeCatDictTMapper.selectList(new EntityWrapper<>());
        ArrayList<Cat> catInfo = new ArrayList<>();
        for (MtimeCatDictT catT : cats) {
            Cat cat = new Cat();
            cat.setCatId(catT.getUuid().toString());
            cat.setCatName(catT.getShowName());
            catInfo.add(cat);
        }
        List<MtimeSourceDictT> sources = mtimeSourceDictTMapper.selectList(new EntityWrapper<>());
        ArrayList<Source> sourceInfo = new ArrayList<>();
        for (MtimeSourceDictT sourceT : sources) {
            Source source = new Source();
            source.setSourceId(sourceT.getUuid().toString());
            source.setSourceName(sourceT.getShowName());
            sourceInfo.add(source);
        }
        List<MtimeYearDictT> years = mtimeYearDictTMapper.selectList(new EntityWrapper<>());
        ArrayList<Year> yearInfo = new ArrayList<>();
        for (MtimeYearDictT yearT : years) {
            Year year = new Year();
            year.setYearId(yearT.getUuid().toString());
            year.setYearName(yearT.getShowName());
            yearInfo.add(year);
        }
        data.setCatInfo(catInfo);
        data.setSourceInfo(sourceInfo);
        data.setYearInfo(yearInfo);
        return data;
    }

    @Override
    public String getNameById(Integer filmId) {
        MtimeFilmT mtimeFilmT = mtimeFilmTMapper.selectById(filmId);
        return mtimeFilmT.getFilmName();
    }

}
