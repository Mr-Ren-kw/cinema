package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimeHallFilmInfoT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.stylefeng.guns.rest.common.persistence.model.field.Film;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 影厅电影信息表 Mapper 接口
 * </p>
 *
 * @author renkw
 * @since 2019-10-12
 */
public interface MtimeHallFilmInfoTMapper extends BaseMapper<MtimeHallFilmInfoT> {

    Film selectFilmInfoByFilmId(@Param("filmId") int filmId);
}
