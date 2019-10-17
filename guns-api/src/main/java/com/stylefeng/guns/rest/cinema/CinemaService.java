package com.stylefeng.guns.rest.cinema;

public interface CinemaService {
    // 获取影院列表查询条件
    Object getCodition(int brandId, int hallType, int areaId);
    // 获取播放场次接口
    Object getFields(int cinemaId);
    //获取影院名字
    String getNameById(Integer cinemaId);

    String getFieldTimeById(Integer fieldId);
}
