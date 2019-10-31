package com.stylefeng.guns.rest.cinema;

import com.stylefeng.guns.rest.cinema.vo.FieldMsgForOrder;
import com.stylefeng.guns.rest.cinema.vo.BaseCinemaRespVo;
import com.stylefeng.guns.rest.cinema.vo.CinemaVo;

public interface CinemaService {
    // 获取影院列表查询条件
    Object getCodition(int brandId, int hallType, int areaId);
    // 获取播放场次接口
    Object getFields(int cinemaId);
    // 获取影院场次座位信息字符串
    String getHallSeatIds(int fieldId);
    // 根据fieldId查询field表获取所有信息
    FieldMsgForOrder getFieldMsgById(int fieldId);
    // 通过fieldId查询场次的开始时间
    String getFieldTimeById(int fieldId);
    // 通过cinemaId获得影院的名称
    String getCinemaNameById(int cinemaId);
    //根据条件查询所有影院
    BaseCinemaRespVo getCinemas(CinemaVo cinemaVo);


    BaseCinemaRespVo getFieldInfo(int cinemaId, int fieldId);
    //获取影院名字
    String getNameById(Integer cinemaId);

    String getFieldTimeById(Integer fieldId);

    String getCinemaAddressById(Integer cinemaId);

    String getImgAddressById(Integer cinemaId);
}
