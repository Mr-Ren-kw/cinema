package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimePromo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.stylefeng.guns.rest.promo.vo.PromoVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-10-18
 */
public interface MtimePromoMapper extends BaseMapper<MtimePromo> {

    MtimePromo[] queryAllPromos();

    MtimePromo[] queryPromosByCid(@Param("cid") Integer cinemaId);

    PromoVO[] queryPromoByCidStatus(@Param("cid") Integer cinemaId, @Param("status") int status);
}
