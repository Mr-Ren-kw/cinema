package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author renkw
 * @since 2019-10-15
 */
public interface MoocOrderTMapper extends BaseMapper<MoocOrderT> {

    // 获取当前场次已经出售的座位号
    List<String> getSoldSeatList(@Param("fieldId") int fieldId);
    // 生成新的订单
    int insertNewOrder(@Param("new") MoocOrderT moocOrderT);
    // 获取通过订单信息
    MoocOrderT selectMoocOrderTById(@Param("uuid") String uuid);

    MoocOrderT selectByUuid(@Param("uuid") String orderId);

    void updateOrderStatusByUuid(@Param("uuid") String orderId, @Param("status") int i);

}
