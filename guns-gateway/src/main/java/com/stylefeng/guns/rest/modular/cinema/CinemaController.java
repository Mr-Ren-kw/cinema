package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.cinema.vo.BaseCinemaRespVo;
import com.stylefeng.guns.rest.cinema.vo.CinemaVo;
import com.stylefeng.guns.rest.cinema.vo.ConditionVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cinema")
public class CinemaController {

   @Reference(interfaceClass = CinemaService.class)
   private CinemaService cinemaService;

    @GetMapping("/getCinemas")
    public BaseCinemaRespVo getCinemas(CinemaVo cinemaVo){
        BaseCinemaRespVo baseCinemaRespVo = cinemaService.getCinemas(cinemaVo);
        return baseCinemaRespVo;
    }

    @GetMapping("/getCondition")
    public BaseCinemaRespVo getCondition(ConditionVo conditionVo) {
        return BaseCinemaRespVo.ok(cinemaService.getCodition(conditionVo.getBrandId(), conditionVo.getHallType(), conditionVo.getAreaId()));
    }

    @RequestMapping("/getFields")
    public BaseCinemaRespVo getFields(int cinemaId) {
        BaseCinemaRespVo respVo = BaseCinemaRespVo.ok(cinemaService.getFields(cinemaId));
        respVo.setImgPre("http://img.meetingshop.cn/");
        return respVo;
    }

    @RequestMapping("/getFieldInfo")
    public BaseCinemaRespVo getFieldInfo(int cinemaId,int fieldId){
        BaseCinemaRespVo respVo = cinemaService.getFieldInfo(cinemaId,fieldId);
        return respVo;
    }
}
