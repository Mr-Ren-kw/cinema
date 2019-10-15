package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimeUserT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-10-15
 */
public interface MtimeUserTMapper extends BaseMapper<MtimeUserT> {

    Integer selectIdByNameAndPwd(@Param("username") String username,@Param("password") String password);
}
