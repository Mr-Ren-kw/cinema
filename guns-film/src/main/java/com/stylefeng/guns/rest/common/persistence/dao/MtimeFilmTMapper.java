package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimeFilmT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 影片主表 Mapper 接口
 * </p>
 *
 */
public interface MtimeFilmTMapper extends BaseMapper<MtimeFilmT> {
    // 根据id获取电影名称
    String getFilmNameById(@Param("id") int filmId);
}
