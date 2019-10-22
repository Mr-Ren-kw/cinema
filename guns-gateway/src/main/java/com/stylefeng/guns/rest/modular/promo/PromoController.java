package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.promo.PromoService;
import com.stylefeng.guns.rest.promo.consistent.CachePromoPrefix;
import com.stylefeng.guns.rest.promo.vo.PromoOrderRespVo;
import com.stylefeng.guns.rest.promo.vo.PromoRespVo;
import com.stylefeng.guns.rest.promo.vo.PromoStockRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/promo")
public class PromoController {
    @Reference(interfaceClass = PromoService.class)
    PromoService promoService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    Jedis jedis;

    @GetMapping("/getPromo")
    public PromoRespVo getPromo(Integer cinemaId) {
        return promoService.getPromoList(cinemaId);
    }

    @RequestMapping("/createOrder")
    public PromoOrderRespVo createPromoOrder(@RequestParam(required = true,name = "promoId")int promoId,
                                             @RequestParam(required = true,name = "amount")int amount,
                                             @RequestParam(required = true,name = "token")String promoToken,
                                             HttpServletRequest request) {
        // 校验参数
        if (amount <= 0) {
            return PromoOrderRespVo.fail();
        }
        int userId = jwtTokenUtil.parseToken(request);
        // 验证秒杀令牌
        String tokenKey = CachePromoPrefix.PROMO_TOKEN_PREFIX + "userId_" + userId + "_promoId_" + promoId;
        String token = jedis.get(tokenKey);
        if (token == null || !token.equals(promoToken)) {
            return PromoOrderRespVo.fail();
        }
        // 初始化一条流水表
        String stockLogId = promoService.insertNewStockLog(promoId, amount);
        // 进行事务处理
        boolean flag = promoService.insertNewPromoOrder(promoId, amount, userId, stockLogId);
        if (flag) {
            jedis.del(tokenKey);
            return PromoOrderRespVo.ok();
        } else {
            return PromoOrderRespVo.fail();
        }
    }

    @GetMapping("/publishPromoStock")
    public PromoStockRespVo publishPromoStock(@RequestParam(required = false, name = "cinemaId") Integer cinemaId) {
        // 判断之前是否刷新过缓存
        if (jedis.exists(CachePromoPrefix.PROMO_STOCK_CACHE_PREFIX)) {
            return PromoStockRespVo.success("已经刷新过了");
        }
        PromoStockRespVo stockRespVo = promoService.publishPromoStock(cinemaId);
        jedis.set(CachePromoPrefix.PROMO_STOCK_CACHE_PREFIX, "success");
        return stockRespVo;
    }

    @GetMapping("/generateToken")
    public PromoStockRespVo generateToken(@RequestParam(required = true, name = "promoId") int promoId, HttpServletRequest request) {
        int userId = jwtTokenUtil.parseToken(request);
        // 先判断库存是否为空
        Boolean isEmpty = jedis.exists(CachePromoPrefix.PROMO_STOCK_EMPTY_PREFIX + promoId);
        if (isEmpty) {
            return PromoStockRespVo.fail("库存已空！");
        }
        // 再判断缓存中的令牌数量是否为空
        Boolean exists = jedis.exists(CachePromoPrefix.PROMO_TOKEN_EMPTY_PREFIX + promoId);
        if (exists) {
            return PromoStockRespVo.fail("获取失败");
        }
        // 不为空，获取令牌
        String token = promoService.generateToken(promoId, userId);
        return PromoStockRespVo.success(token);
    }
}
