package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.film.FilmService;
import com.stylefeng.guns.rest.film.vo.*;
import lombok.experimental.var;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.stylefeng.guns.rest.film.vo.ConditionData;
import com.stylefeng.guns.rest.film.vo.FilmConditionVo;
import com.stylefeng.guns.rest.film.vo.FilmPage;
import com.stylefeng.guns.rest.film.vo.IndexData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/14 23:28
 */
@Service(interfaceClass = FilmService.class)
@Component
public class FilmServiceImpl implements FilmService {
    @Autowired
    MtimeFilmTMapper mtimeFilmTMapper;
    @Autowired
    MtimeFilmActorTMapper mtimeFilmActorTMapper;
    @Autowired
    MtimeFilmInfoTMapper mtimeFilmInfoTMapper;
    @Autowired
    MtimeHallFilmInfoTMapper mtimeHallFilmInfoTMapper;
    @Autowired
    MtimeSourceDictTMapper mtimeSourceDictTMapper;
    @Autowired
    MtimeActorTMapper mtimeActorTMapper;
    @Autowired
    MtimeBannerTMapper mtimeBannerTMapper;
    @Autowired
    MtimeCatDictTMapper mtimeCatDictTMapper;
    @Autowired
    MtimeYearDictTMapper mtimeYearDictTMapper;

    private static final Integer HOT_FILM = 8;
    private static final Integer SOON_FILM = 8;
    private static final Integer BOX_RANK = 9;
    private static final Integer EXCEPT_RANK = 7;
    private static final Integer TOP_100 = 9;

    @Override
    public List<FilmInfoVo> getFilms(FilmsConditionVo filmsConditionVo) {

        filmsConditionVo.setNowPage(filmsConditionVo.getOffset() + 1);
        Page<MtimeFilmT> page = new Page<>();
        page.setSize(filmsConditionVo.getPageSize());
        page.setCurrent(filmsConditionVo.getNowPage());

        // 多条件查询
        EntityWrapper<MtimeFilmT> entityWrapper = new EntityWrapper<>();
        // 通过showType查询
        entityWrapper.eq("film_status",filmsConditionVo.getShowType());
        // 判断sortId，排序方式，1-按热门搜索，2-按时间搜索，3-按评价搜索
        switch (filmsConditionVo.getSortId()){
            case 1 :
                // 热门搜索数据库表里没这个值……
                break;
            case 2 :
                ArrayList<String> sortByTime = new ArrayList<>();
                sortByTime.add("film_time");
                entityWrapper.orderDesc(sortByTime);
                break;
            case 3 :
                ArrayList<String> sortByScore = new ArrayList<>();
                sortByScore.add("film_score");
                entityWrapper.orderDesc(sortByScore);
                break;
        }
        // 查询类型编号carId
        Integer catId = filmsConditionVo.getCatId();
        if (catId != 99){
            // 如果不等于99就进行查询
            String catIdStr = String.valueOf(catId);
            catIdStr = "#" + catIdStr + "#";
            entityWrapper.like("film_cats",catIdStr);
        }
        // 查询区域编号sourceId
        Integer sourceId = filmsConditionVo.getSourceId();
        if (sourceId != 99){
            entityWrapper.eq("film_source",sourceId);
        }
        // 查询年代编号yearId
        Integer yearId = filmsConditionVo.getYearId();
        if (yearId != 99){
            entityWrapper.eq("film_date",yearId);
        }

        List<MtimeFilmT> mtimeFilmTS = mtimeFilmTMapper.selectPage(page, entityWrapper);
        List<FilmInfoVo> filmInfoVos = convert2FilmInfoList(mtimeFilmTS);
        return filmInfoVos;
    }


    private List<FilmInfoVo> convert2FilmInfoList(List<MtimeFilmT> mtimeFilmTS) {
        ArrayList<FilmInfoVo> filmInfoVos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(mtimeFilmTS)) {
            for (MtimeFilmT mtimeFilmT : mtimeFilmTS) {
                FilmInfoVo filmInfoVo = new FilmInfoVo();
                filmInfoVo.setFilmId(mtimeFilmT.getUuid());
                filmInfoVo.setFilmName(mtimeFilmT.getFilmName());
                filmInfoVo.setFilmType(mtimeFilmT.getFilmType());
                filmInfoVo.setImgAddress(mtimeFilmT.getImgAddress());
                filmInfoVo.setFilmScore(mtimeFilmT.getFilmScore());

                filmInfoVos.add(filmInfoVo);
            }
        }
        return filmInfoVos;
    }

    @Override
    public FilmQueryByIdVO queryFilmById(Integer filmId) {
        MtimeFilmT mtimeFilmT = mtimeFilmTMapper.selectById(filmId);
        FilmQueryByIdVO filmQueryByIdVO = new FilmQueryByIdVO();
        filmQueryByIdVO.setFilmName(mtimeFilmT.getFilmName());  // fileName

        MtimeFilmInfoT mtimeFilmInfoT = new MtimeFilmInfoT();
        mtimeFilmInfoT.setFilmId(String.valueOf(filmId));
        MtimeFilmInfoT mtimeFilmInfoT2 = mtimeFilmInfoTMapper.selectOne(mtimeFilmInfoT);
        filmQueryByIdVO.setFilmEnName(mtimeFilmInfoT2.getFilmEnName()); // EnName

        filmQueryByIdVO.setImgAddress(mtimeFilmT.getImgAddress());  // imgAddress

        filmQueryByIdVO.setScore(mtimeFilmT.getFilmScore());    // score

        filmQueryByIdVO.setScoreNum(mtimeFilmInfoT2.getFilmScoreNum() + "万人评分"); // scoreNum

        // 这边要判断是万还是亿
        Integer filmBoxOffice = mtimeFilmT.getFilmBoxOffice();
        if (filmBoxOffice < 10000){
            filmQueryByIdVO.setTotalBox(filmBoxOffice + " 万"); // totalBox
        }else{
            Integer fbo = filmBoxOffice/10000;
            filmQueryByIdVO.setTotalBox(fbo + " 亿");
        }


        MtimeHallFilmInfoT mtimeHallFilmInfoT = new MtimeHallFilmInfoT();
        mtimeHallFilmInfoT.setFilmId(filmId);
        MtimeHallFilmInfoT mtimeHallFilmInfoT1 = mtimeHallFilmInfoTMapper.selectOne(mtimeHallFilmInfoT);
        filmQueryByIdVO.setInfo01(mtimeHallFilmInfoT1.getFilmCats());   // info1

        String info02;
        MtimeSourceDictT mtimeSourceDictT = mtimeSourceDictTMapper.selectById(mtimeFilmT.getFilmArea());
        info02 = mtimeSourceDictT.getShowName() + " / " + mtimeFilmInfoT2.getFilmLength() + " 分钟";
        filmQueryByIdVO.setInfo02(info02);  // info2

        String info03;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        info03 = sdf.format( mtimeFilmT.getFilmTime() );
        info03 = info03 + " " + mtimeSourceDictT.getShowName() + "上映";
        filmQueryByIdVO.setInfo03(info03);  // info03

        FilmDetail filmDetail = new FilmDetail();
        filmDetail.setBiopgraphy(mtimeFilmInfoT2.getBiography()); // biography
        filmDetail.setFilmId(String.valueOf(filmId));   //  应该放在这里，之前放错地方了（不是放错地方，是接口文档和示例不一致）
        FilmActors filmActors = new FilmActors();
        // 演员表里搜索找出导演（没有导演这个表）
        MtimeActorT mtimeActorT = mtimeActorTMapper.selectById(mtimeFilmInfoT2.getDirectorId());
        FilmActor filmActor = new FilmActor();
        filmActor.setDirectorName(mtimeActorT.getActorName());
        filmActor.setImgAddress(mtimeActorT.getActorImg());
        filmActors.setDirector(filmActor);

        List<MtimeFilmActorT> mtimeFilmActorTS = mtimeFilmActorTMapper.selectList(new EntityWrapper<MtimeFilmActorT>()
            .eq("film_id",filmId));
        List<FilmActor> filmActorsList = new ArrayList<>();
        for (MtimeFilmActorT mtimeFilmActorT : mtimeFilmActorTS) {
            FilmActor filmActor1 = new FilmActor();
            // 通过actor_id找演员名
            Integer actorId = mtimeFilmActorT.getActorId();
            MtimeActorT mtimeActorT1 = mtimeActorTMapper.selectById(actorId);
            filmActor1.setImgAddress(mtimeActorT1.getActorImg());
            filmActor1.setDirectorName(mtimeActorT1.getActorName());
            filmActor1.setRoleName(mtimeFilmActorT.getRoleName());

            filmActorsList.add(filmActor1);
        }
        filmActors.setActors(filmActorsList);
        filmDetail.setActors(filmActors);


        FilmImgVO filmImgVO = new FilmImgVO();
        // filmImgVO.setMainImg(mtimeHallFilmInfoT1.getImgAddress());
        // 其他四个图不知道在哪里（找到了，在mtimeFilmInfoT的film_imgs里）上面这句有问题，五个图都在film_imgs里
        String[] split = mtimeFilmInfoT2.getFilmImgs().split(",");
        // 必须保证图片在放进去的时候有五张，不然一张都不会显示
        if(split.length >= 5) {
            filmImgVO.setMainImg(split[0]);
            filmImgVO.setImg01(split[1]);
            filmImgVO.setImg02(split[2]);
            filmImgVO.setImg03(split[3]);
            filmImgVO.setImg04(split[4]);
        }
        filmQueryByIdVO.setFilmId(String.valueOf(filmId));
        filmDetail.setImgVO(filmImgVO); // filmDetail-ImgVO
        filmQueryByIdVO.setImgVO(filmImgVO);    // imgVO
        filmQueryByIdVO.setInfo04(filmDetail);   // info4
        return filmQueryByIdVO;
    }

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
}
