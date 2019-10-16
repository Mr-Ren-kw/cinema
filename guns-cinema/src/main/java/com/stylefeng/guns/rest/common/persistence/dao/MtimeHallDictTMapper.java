package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimeHallDictT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.stylefeng.guns.rest.common.persistence.model.codition.HallType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 地域信息表 Mapper 接口
 * </p>
 *
 * @author renkw
 * @since 2019-10-12
 */
public interface MtimeHallDictTMapper extends BaseMapper<MtimeHallDictT> {

    List<HallType> getHallList();
    // 获取该影厅的座位信息对应的json地址
    String getSeatAddressById(@Param("hallId") int hallId);
}
