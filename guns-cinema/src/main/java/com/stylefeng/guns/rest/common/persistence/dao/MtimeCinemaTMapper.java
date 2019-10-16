package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimeCinemaT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 影院信息表 Mapper 接口
 * </p>
 *
 * @author renkw
 * @since 2019-10-12
 */
public interface MtimeCinemaTMapper extends BaseMapper<MtimeCinemaT> {
    // 通过cinemaId查询影院名称
    String getCinemaNameById(@Param("cinemaId") int cinemaId);
}
