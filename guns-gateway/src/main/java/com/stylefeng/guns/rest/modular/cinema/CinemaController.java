package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.cinema.vo.BaseCinemaRespVo;
import com.stylefeng.guns.rest.cinema.vo.ConditionVo;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import com.stylefeng.guns.rest.modular.auth.util.UserIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/cinema")
public class CinemaController {
    @Reference(interfaceClass = CinemaService.class)
    private CinemaService cinemaService;

    @Autowired
    private UserIdUtil userIdUtil;

    @GetMapping("/getCondition")
    public BaseCinemaRespVo getCondition(ConditionVo conditionVo) {
        return BaseCinemaRespVo.ok(cinemaService.getCodition(conditionVo.getBrandId(), conditionVo.getHallType(), conditionVo.getAreaId()));
    }

    @RequestMapping("/getFields")
    public BaseCinemaRespVo getFields(int cinemaId, HttpServletRequest request) {
        int id = userIdUtil.getUserIdByToken(request);
        System.out.println("id = " + id);
        BaseCinemaRespVo respVo = BaseCinemaRespVo.ok(cinemaService.getFields(cinemaId));
        respVo.setImgPre("http://img.meetingshop.cn/");
        return respVo;
    }
}
