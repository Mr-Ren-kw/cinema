package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author renkw
 * @since 2019-10-15
 */
public interface MoocOrderTMapper extends BaseMapper<MoocOrderT> {

    MoocOrderT selectByUuid(@Param("uuid") String orderId);

    void updateOrderStatusByUuid(@Param("uuid") String orderId, @Param("status") int i);
}
