package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeUserTMapper;
import com.stylefeng.guns.rest.user.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Service(interfaceClass = MyUserService.class)
public class MyUserServiceImpl implements MyUserService {

    @Autowired
    MtimeUserTMapper userTMapper;

    @Override
    public Integer login(String username, String password) {
        // 查询用户表获取id
        String encrypt = MD5Util.encrypt(password);
        System.out.println(encrypt);
        Integer userId = userTMapper.selectIdByNameAndPwd(username, encrypt);
        return userId;
    }
}
