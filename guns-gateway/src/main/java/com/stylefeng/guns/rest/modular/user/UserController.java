package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.user.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Reference(interfaceClass = UserService.class,check = false)
    UserService userService;

    @RequestMapping("/test")
    public String getTestMsg(String msg) {
        return userService.sendMsg(msg);
    }
}
