package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.promo.PromoService;
import com.stylefeng.guns.rest.promo.vo.PromoOrderVo;
import com.stylefeng.guns.rest.promo.vo.PromoRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/promo")
public class PromoController {
    @Reference(interfaceClass = PromoService.class)
    PromoService promoService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @GetMapping("/getPromo")
    public PromoRespVo getPromo(Integer cinemaId) {
        return promoService.getPromoList(cinemaId);
    }

    @PostMapping("/createOrder")
    public PromoRespVo createPromoOrder(@RequestBody PromoOrderVo promoOrderVo, HttpServletRequest request) {
        int userId = jwtTokenUtil.parseToken(request);
        return promoService.createPromoOrder(promoOrderVo,userId);

    }

/*    @RequestMapping("/publishPromoStock")
    public PromoRespVo publishPromoStock(Integer cinemaId){

    }*/
}
