package com.stylefeng.guns.rest.persistence.dao;

import com.stylefeng.guns.rest.persistence.model.MtimeUserT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.stylefeng.guns.rest.user.vo.UserInfoVO;
import com.stylefeng.guns.rest.user.vo.UserRegisterVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author yyt
 * @since 2019-10-14
 */
public interface MtimeUserTMapper extends BaseMapper<MtimeUserT> {
    MtimeUserT queryUserByUsername(@Param("username") String username);

    Integer updateUserInfo(@Param("userInfo") UserInfoVO userInfo);

}
