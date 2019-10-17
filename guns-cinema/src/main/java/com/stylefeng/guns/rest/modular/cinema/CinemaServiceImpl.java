package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.cinema.vo.BaseCinemaRespVo;
import com.stylefeng.guns.rest.cinema.vo.CinemaVo;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.MtimeCinemaT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeFieldT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeHallDictT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeHallFilmInfoT;
import com.stylefeng.guns.rest.common.persistence.model.cinema.Cinema;
import com.stylefeng.guns.rest.common.persistence.model.codition.Area;
import com.stylefeng.guns.rest.common.persistence.model.codition.Brand;
import com.stylefeng.guns.rest.common.persistence.model.codition.CoditionData;
import com.stylefeng.guns.rest.common.persistence.model.codition.HallType;
import com.stylefeng.guns.rest.common.persistence.model.field.CinemaInfo;
import com.stylefeng.guns.rest.common.persistence.model.field.FieldData;
import com.stylefeng.guns.rest.common.persistence.model.field.Film;
import com.stylefeng.guns.rest.common.persistence.model.hall.HallInfo;
import com.stylefeng.guns.rest.common.persistence.model.hall.ResultFieldInfo;
import com.stylefeng.guns.rest.order.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import com.stylefeng.guns.rest.common.persistence.model.field.FilmField;
import java.util.LinkedList;
import java.util.List;

@Component
@Service(interfaceClass = CinemaService.class)
public class CinemaServiceImpl implements CinemaService {

    @Autowired
    MtimeAreaDictTMapper areaDictTMapper;

    @Autowired
    MtimeBrandDictTMapper brandDictTMapper;

    @Autowired
    MtimeHallDictTMapper hallDictTMapper;

    @Autowired
    MtimeCinemaTMapper cinemaTMapper;

    @Autowired
    MtimeFieldTMapper fieldTMapper;

    @Autowired
    MtimeHallFilmInfoTMapper hallFilmInfoTMapper;

    @Reference(interfaceClass = OrderService.class)
    OrderService orderService;


    @Override
    public Object getCodition(int brandId, int hallType, int areaId) {
        CoditionData coditionData = new CoditionData();
        // 查询area
        List<Area> areaList = areaDictTMapper.getAreaList();
        for (Area area : areaList) {
            if (area.getAreaId() == areaId) {
                area.setActive(true);
            }
        }
        coditionData.setAreaList(areaList);
        // 查询brand
        List<Brand> brandList = brandDictTMapper.getBrandList();
        for (Brand brand : brandList) {
            if (brand.getBrandId() == brandId) {
                brand.setActive(true);
            }
        }
        coditionData.setBrandList(brandList);
        // 查询hall_type
        List<HallType> hallTypeList = hallDictTMapper.getHallList();
        for (HallType type : hallTypeList) {
            if (type.getHalltypeId() == hallType) {
                type.setActive(true);
            }
        }
        coditionData.setHalltypeList(hallTypeList);
        return coditionData;
    }

    @Override
    public Object getFields(int cinemaId) {
        FieldData fieldData = new FieldData();
        // 查询cinema表，封装cinemaInfo
        MtimeCinemaT mtimeCinemaT = cinemaTMapper.selectById(cinemaId);
        CinemaInfo cinemaInfo = new CinemaInfo();
        cinemaInfo.setCinemaId(mtimeCinemaT.getUuid());
        cinemaInfo.setCinemaName(mtimeCinemaT.getCinemaName());
        cinemaInfo.setCinemaPhone(mtimeCinemaT.getCinemaPhone());
        cinemaInfo.setCinemaAdress(mtimeCinemaT.getCinemaAddress());
        cinemaInfo.setImgUrl(mtimeCinemaT.getImgAddress());
        fieldData.setCinemaInfo(cinemaInfo);
        // 封装filmList
        // 先根据cinemaId查询field表获取该影院的电影编号
        List<Integer> filmIdList = fieldTMapper.selectFilmIdListByCinemaId(cinemaId);
        List<Film> filmList = new LinkedList<>();
        for (Integer filmId : filmIdList) {
            // 根据每个电影编号查询电影信息
            Film film = hallFilmInfoTMapper.selectFilmInfoByFilmId(filmId);
            // 根据cinemaId和filmId查询field表
            List<FilmField> filmFields = fieldTMapper.selectFieldListByCinemaIdAndFilmId(cinemaId, filmId);
            for (FilmField filmField : filmFields) {
                filmField.setLanguage(film.getFilmType());
            }
            film.setFilmFields(filmFields);
            filmList.add(film);
        }
        fieldData.setFilmList(filmList);
        return fieldData;
    }

    @Override
    public BaseCinemaRespVo getCinemas(CinemaVo cinemaVo) {
        EntityWrapper<MtimeCinemaT> entityWrapper = new EntityWrapper<>();
        Page<MtimeCinemaT> page = new Page<>(cinemaVo.getNowPage(), cinemaVo.getPageSize());
        // 判断是否传入查询条件 -> brandId,areaI`d,hallType 是否==99 order by id desc
        if (cinemaVo.getBrandId() != 99) {
            entityWrapper.eq("brand_id", cinemaVo.getBrandId());
            //where brand_id  = cinemaVo.getBrandId()
        }
        if (cinemaVo.getAreaId() != 99) {
            entityWrapper.eq("area_id", cinemaVo.getAreaId());
        }
        if (cinemaVo.getHallType() != 99) {
            entityWrapper.like("hall_ids", "%" + cinemaVo.getHallType() + "%");
        }
        List<MtimeCinemaT> mtimeCinemaTS = cinemaTMapper.selectPage(page, entityWrapper);
        ArrayList<Cinema> cinemas = new ArrayList<>();
        for (MtimeCinemaT cinemaT : mtimeCinemaTS) {
            Cinema cinema = new Cinema();
            cinema.setCinemaAddress(cinemaT.getCinemaAddress());
            cinema.setCinemaName(cinemaT.getCinemaName());
            cinema.setMinimumPrice(cinemaT.getMinimumPrice());
            cinema.setUuid(cinemaT.getUuid());
            cinemas.add(cinema);
        }
        BaseCinemaRespVo<Object> baseCinemaRespVo = new BaseCinemaRespVo<>();
        baseCinemaRespVo.setData(cinemas);
        baseCinemaRespVo.setImgPre("http://img.meetingshop.cn/");
        baseCinemaRespVo.setMsg("");
        baseCinemaRespVo.setStatus(0);
        baseCinemaRespVo.setNowPage(cinemaVo.getNowPage());
        int totalPage = 0;
        if (cinemas.size() % cinemaVo.getPageSize() == 0) {
            totalPage = cinemas.size() / cinemaVo.getPageSize();
        } else {
            totalPage = cinemas.size() / cinemaVo.getPageSize() + 1;
        }
        baseCinemaRespVo.setTotalPage(totalPage);
        return baseCinemaRespVo;


    }

    @Override
    public BaseCinemaRespVo getFieldInfo(int cinemaId, int fieldId) {
        ResultFieldInfo resultFieldInfo = new ResultFieldInfo();
        MtimeCinemaT mtimeCinemaT = cinemaTMapper.selectById(cinemaId);
        CinemaInfo cinemaInfo = new CinemaInfo();
        cinemaInfo.setCinemaAdress(mtimeCinemaT.getCinemaAddress());
        cinemaInfo.setCinemaId(cinemaId);
        cinemaInfo.setCinemaName(mtimeCinemaT.getCinemaName());
        cinemaInfo.setCinemaPhone(mtimeCinemaT.getCinemaPhone());
        cinemaInfo.setImgUrl(mtimeCinemaT.getImgAddress());
        resultFieldInfo.setCinemaInfo(cinemaInfo);
        MtimeFieldT fieldT = fieldTMapper.selectById(fieldId);
        Integer filmId = fieldT.getFilmId();
        MtimeHallFilmInfoT mtimeHallFilmInfoT = new MtimeHallFilmInfoT();
        mtimeHallFilmInfoT.setFilmId(filmId);
        MtimeHallFilmInfoT mtimeHallFilmInfoT1 = hallFilmInfoTMapper.selectOne(mtimeHallFilmInfoT);
        Film filmInfo = new Film();
        BeanUtils.copyProperties(mtimeHallFilmInfoT1,filmInfo);
        Integer hallId = fieldT.getHallId();
        MtimeHallDictT mtimeHallDictT = hallDictTMapper.selectById(hallId);
        HallInfo hallInfo = new HallInfo();
        hallInfo.setHallFieldId(fieldId);
        hallInfo.setHallName(fieldT.getHallName());
        hallInfo.setPrice(fieldT.getPrice());
        hallInfo.setSeatFile(mtimeHallDictT.getSeatAddress());
        String soldSeats =  orderService.getSoldSeats(fieldId);
        hallInfo.setSoldSeats(soldSeats);
        resultFieldInfo.setFilmInfo(filmInfo);
        resultFieldInfo.setHallInfo(hallInfo);

        BaseCinemaRespVo<ResultFieldInfo> respVo = new BaseCinemaRespVo<>();
        respVo.setData(resultFieldInfo);
        respVo.setStatus(0);
        respVo.setImgPre("http://img.meetingshop.cn/");

        return respVo;
    }


}
