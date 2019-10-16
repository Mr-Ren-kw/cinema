package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimeFieldT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.stylefeng.guns.rest.common.persistence.model.field.FilmField;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 放映场次表 Mapper 接口
 * </p>
 *
 * @author renkw
 * @since 2019-10-12
 */
public interface MtimeFieldTMapper extends BaseMapper<MtimeFieldT> {
    // 通过cinemaId查询该影院的电影filmId
    List<Integer> selectFilmIdListByCinemaId(@Param("cinemaId") int cinemaId);
    // 通过cinemaId和filmId查询对应的field
    List<FilmField> selectFieldListByCinemaIdAndFilmId(@Param("cinemaId") int cinemaId,@Param("filmId") int filmId);
}
