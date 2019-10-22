package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimeStockLog;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author renkw
 * @since 2019-10-22
 */
public interface MtimeStockLogMapper extends BaseMapper<MtimeStockLog> {
    // 更新库存流水线中的状态
    int updateStockStatusById(@Param("stockId") String stockLogId,@Param("status") int status);
}
