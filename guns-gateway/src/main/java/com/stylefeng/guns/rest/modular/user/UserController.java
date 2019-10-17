package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.user.UserService;
import com.stylefeng.guns.rest.user.vo.BaseRespVo;
import com.stylefeng.guns.rest.user.vo.UserInfoVO;
import com.stylefeng.guns.rest.user.vo.UserRegisterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("user")
public class UserController {
    @Reference(interfaceClass = UserService.class)
    private UserService userService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    Jedis jedis;
    @RequestMapping("register")
    public BaseRespVo getTestMsg(UserRegisterVO userInfo) {
        if (userInfo == null || userInfo.getUsername() == null || userInfo.getPassword() == null) {
            return new BaseRespVo("系统出现异常，请联系管理员", 999);
        }
        boolean check = userService.checkUser(userInfo.getUsername());
        if (check) {
            return new BaseRespVo("用户已存在", 1);
        }
        Integer res = userService.register(userInfo);
        return new BaseRespVo("注册成功", res);
    }

    @RequestMapping("check")
    public BaseRespVo check(String username) {
        try {
            boolean res = userService.checkUser(username);
            if (res) {
                // 如果存在
                return new BaseRespVo("用户已经注册", 1);
            } else {
                return new BaseRespVo("用户名不存在", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseRespVo("系统出现异常，请联系管理员", 999);
        }
    }

    @RequestMapping("logout")
    public BaseRespVo logout(HttpServletRequest request) {
        try{
            String token = jwtTokenUtil.getToken(request);
            jedis.del(token);
            return new BaseRespVo("成功退出", 0);
        } catch (Exception e) {
            return new BaseRespVo("系统出现异常，请联系管理员", 999);
        }
    }

    @RequestMapping("getUserInfo")
    public BaseRespVo getUserInfo(HttpServletRequest request) {
        try {
            int uuid = jwtTokenUtil.parseToken(request);
            UserInfoVO userInfoVO = userService.queryUserInfo(uuid);
            return new BaseRespVo(userInfoVO, 0);
        }catch (Exception e) {
            e.printStackTrace();
            return new BaseRespVo("系统出现异常，请联系管理员", 999);
        }
    }

    @RequestMapping("updateUserInfo")
    public BaseRespVo updateUserInfo(UserInfoVO userInfo, HttpServletRequest request) {
        if(userInfo == null || userInfo.getUuid() == null) {
            return new BaseRespVo("用户信息修改失败", 1);
        }
        try {
            int uuid = jwtTokenUtil.parseToken(request);
            // 检验uuid是否一致
            if(uuid != userInfo.getUuid()) {
                return new BaseRespVo("用户信息修改失败", 1);
            }
            UserInfoVO userInfoVO = userService.updateUserInfo(userInfo);
            return new BaseRespVo(userInfoVO, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseRespVo("系统出现异常，请联系管理员", 999);
        }
    }
}
