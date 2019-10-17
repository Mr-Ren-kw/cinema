package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.persistence.dao.MtimeUserTMapper;
import com.stylefeng.guns.rest.persistence.model.MtimeUserT;
import com.stylefeng.guns.rest.user.UserService;
import com.stylefeng.guns.rest.user.validator.AuthRequest;
import com.stylefeng.guns.rest.user.validator.Credence;
import com.stylefeng.guns.rest.user.vo.UserInfoVO;
import com.stylefeng.guns.rest.user.vo.UserRegisterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Service(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {
    @Autowired
    MtimeUserTMapper userMapper;

    @Override
    public Integer register(UserRegisterVO userInfo) {
        MtimeUserT mtimeUserT = new MtimeUserT();
        // 加密密码
        String encode = MD5Util.encrypt(userInfo.getPassword());
        mtimeUserT.setUserName(userInfo.getUsername());
        mtimeUserT.setUserPwd(encode);
        mtimeUserT.setEmail(userInfo.getEmail());
        mtimeUserT.setUserPhone(userInfo.getMobile());
        mtimeUserT.setAddress(userInfo.getAddress());
        mtimeUserT.setUpdateTime(new Date());
        mtimeUserT.setBeginTime(new Date());
        Integer res = userMapper.insert(mtimeUserT);
        if(res == 1) {
            return 0;
        }
        return 999;
    }

    /**
     * 检查是否存在username对应的user
     * @param userName
     * @return true, 则存在这个user， 否则false
     */
    @Override
    public boolean checkUser(String userName) {
        MtimeUserT mtimeUserT = userMapper.queryUserByUsername(userName);
        return mtimeUserT != null;
    }

    @Override
    public String queryPasswordByUserName(String credenceName) {
        MtimeUserT mtimeUserT = userMapper.queryUserByUsername(credenceName);
        if(mtimeUserT == null) {
            return null;
        }
        return mtimeUserT.getUserPwd();
    }

    @Override
    public Integer login(Credence credence) {
        if(credence == null || credence.getCredenceName() == null || credence.getCredenceCode() == null) {
            return null;
        }
        MtimeUserT mtimeUserT = userMapper.queryUserByUsername(credence.getCredenceName());
        if(mtimeUserT == null) {
            return null;
        }
        String password = mtimeUserT.getUserPwd();
        if(password == null) {
            return null;
        }
        String encode = MD5Util.encrypt(credence.getCredenceCode());
        if(password.equals(encode)) {
            return mtimeUserT.getUuid();
        }
        return null;
    }

    @Override
    public UserInfoVO queryUserInfo(int uuid) {
        MtimeUserT mtimeUserT = userMapper.selectById(uuid);
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUuid(mtimeUserT.getUuid());
        userInfoVO.setAddress(mtimeUserT.getAddress());
        userInfoVO.setBiography(mtimeUserT.getBiography());
        userInfoVO.setBirthday(mtimeUserT.getBirthday());
        userInfoVO.setCreateTime(mtimeUserT.getBeginTime());
        userInfoVO.setEmail(mtimeUserT.getEmail());
        userInfoVO.setLifeState(mtimeUserT.getLifeState());
        userInfoVO.setHeadAddress(mtimeUserT.getHeadUrl());
        userInfoVO.setNickname(mtimeUserT.getNickName());
        userInfoVO.setPhone(mtimeUserT.getUserPhone());
        userInfoVO.setSex(mtimeUserT.getUserSex());
        userInfoVO.setUpdateTime(mtimeUserT.getUpdateTime());
        userInfoVO.setUsername(mtimeUserT.getUserName());
        return userInfoVO;
    }

    @Override
    public UserInfoVO updateUserInfo(UserInfoVO userInfo) {
        userMapper.updateUserInfo(userInfo);
        return queryUserInfo(userInfo.getUuid());
    }
}
