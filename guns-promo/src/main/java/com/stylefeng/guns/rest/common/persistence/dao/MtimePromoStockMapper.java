package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author renkw
 * @since 2019-10-18
 */
public interface MtimePromoStockMapper extends BaseMapper<MtimePromoStock> {
    // 根据promoId查询stock
    Integer selectStockByPromoId(@Param("promoId") int promoId);
    // 根据promoId修改库存
    int updateStockByPromoId(@Param("promoId") int promoId,@Param("newStock") int newStock);
}
