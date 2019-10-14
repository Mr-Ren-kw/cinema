package com.stylefeng.guns.rest.cinema;

import com.stylefeng.guns.rest.cinema.vo.BaseCinemaRespVo;
import com.stylefeng.guns.rest.cinema.vo.CinemaVo;

public interface CinemaService {
    // 获取影院列表查询条件
    Object getCodition(int brandId, int hallType, int areaId);
    // 获取播放场次接口
    Object getFields(int cinemaId);
    //根据条件查询所有影院
    BaseCinemaRespVo getCinemas(CinemaVo cinemaVo);


    BaseCinemaRespVo getFieldInfo(int cinemaId, int fieldId);
}
