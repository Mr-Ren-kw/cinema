package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.cinema.vo.FieldMsgForOrder;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.MtimeCinemaT;
import com.stylefeng.guns.rest.common.persistence.model.codition.Area;
import com.stylefeng.guns.rest.common.persistence.model.codition.Brand;
import com.stylefeng.guns.rest.common.persistence.model.codition.CoditionData;
import com.stylefeng.guns.rest.common.persistence.model.codition.HallType;
import com.stylefeng.guns.rest.common.persistence.model.field.CinemaInfo;
import com.stylefeng.guns.rest.common.persistence.model.field.FieldData;
import com.stylefeng.guns.rest.common.persistence.model.field.Film;
import com.stylefeng.guns.rest.common.persistence.model.field.FilmField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    @Autowired
    Jedis jedis;

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
    public String getHallSeatIds(int fieldId) {
        // 查询field表获取hallId
        int hallId = fieldTMapper.selectHallIdByFieldId(fieldId);
        // 先查询redis，如果查不到再去查json文件读取座位信息
        String seatMsg = jedis.get(String.valueOf(hallId));
        if (seatMsg != null) {
            return seatMsg;
        }
        // 通过hallId获取json文件的地址
        String seatAddress = hallDictTMapper.getSeatAddressById(hallId);
        // 通过地址找到对应的json文件来读取

        // 暂时直接读取
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("cgs.json"));
            String s = null;
            while (bufferedReader.readLine() != null) {
                s = bufferedReader.readLine();
                if (s.contains("ids")) {
                    break;
                }
            }
            if (s != null) {
                String[] split = s.split("\"");
                System.out.println("size = " + split.length);
                System.out.println(split[2]);
                seatMsg = split[2];
                // 存储到redis中
                jedis.set(String.valueOf(hallId), seatMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return seatMsg;
    }

    @Override
    public FieldMsgForOrder getFieldMsgById(int fieldId) {
        return fieldTMapper.selectFieldMsgById(fieldId);
    }

    @Override
    public String getFieldTimeById(int fieldId) {
        return fieldTMapper.getFieldTimeById(fieldId);
    }

    @Override
    public String getCinemaNameById(int cinemaId) {
        return cinemaTMapper.getCinemaNameById(cinemaId);
    }
}
