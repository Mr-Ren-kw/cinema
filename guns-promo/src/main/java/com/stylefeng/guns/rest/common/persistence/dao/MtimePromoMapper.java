package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimePromo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author renkw
 * @since 2019-10-18
 */
public interface MtimePromoMapper extends BaseMapper<MtimePromo> {
    // 根据cinemaId查询该影院的秒杀活动，并根据status，0为正常，1为已经领完，2为已经过期
    List<MtimePromo> selectPromoByCinemaId(@Param("cinemaId") Integer cinemaId);
}
