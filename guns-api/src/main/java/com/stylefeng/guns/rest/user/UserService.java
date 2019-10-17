package com.stylefeng.guns.rest.user;

import com.stylefeng.guns.rest.user.validator.Credence;
import com.stylefeng.guns.rest.user.vo.UserInfoVO;
import com.stylefeng.guns.rest.user.vo.UserRegisterVO;

public interface UserService {
    // 用户注册
    Integer register(UserRegisterVO userInfo);
    // 根据username检查对应的user是否存在
    boolean checkUser(String userName);

    String queryPasswordByUserName(String credenceName);

    Integer login(Credence credence);


    UserInfoVO queryUserInfo(int uuid);

    UserInfoVO updateUserInfo(UserInfoVO userInfo);

}
