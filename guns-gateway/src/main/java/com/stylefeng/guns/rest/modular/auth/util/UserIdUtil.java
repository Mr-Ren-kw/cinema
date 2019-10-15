package com.stylefeng.guns.rest.modular.auth.util;

import com.stylefeng.guns.rest.config.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;

@Component
public class UserIdUtil {
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private Jedis jedis;

    /**
     * 通过请求头获取userId，只能在过滤器验证token通过之后使用
     * @param request
     * @return
     */
    public int getUserIdByToken(HttpServletRequest request) {
        String header = request.getHeader(jwtProperties.getHeader());
        String token = header.substring(7);
        String userIdStr = jedis.get(token);
        return Integer.parseInt(userIdStr);
    }
}
