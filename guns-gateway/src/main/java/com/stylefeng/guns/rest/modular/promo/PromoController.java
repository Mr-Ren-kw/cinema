package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.util.concurrent.RateLimiter;
import com.stylefeng.guns.core.exception.GunsException;
import com.stylefeng.guns.core.exception.GunsExceptionEnum;
import com.stylefeng.guns.rest.common.cache.CacheService;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.promo.PromoService;
import com.stylefeng.guns.rest.promo.consistent.CachePromoPrefix;
import com.stylefeng.guns.rest.promo.vo.PromoOrderRespVo;
import com.stylefeng.guns.rest.promo.vo.PromoRespVo;
import com.stylefeng.guns.rest.promo.vo.PromoStockRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@RestController
@RequestMapping("/promo")
public class PromoController {
    @Reference(interfaceClass = PromoService.class)
    PromoService promoService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    Jedis jedis;

    private ExecutorService executorService;

    private RateLimiter rateLimiter;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(200);
        rateLimiter = RateLimiter.create(10);
    }

    @GetMapping("/getPromo")
    public PromoRespVo getPromo(Integer cinemaId) {
        return promoService.getPromoList(cinemaId);
    }

    @GetMapping("/createOrder")
    public PromoOrderRespVo createPromoOrder(@RequestParam(required = true,name = "promoId")Integer promoId,
                                             @RequestParam(required = true,name = "amount")Integer amount,
                                             @RequestParam(required = true,name = "promoToken")String promoToken,
                                             HttpServletRequest request) {
        double time = rateLimiter.acquire();
        if (time < 0) {
            return PromoOrderRespVo.fail();
        }
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
        Future future = executorService.submit(() -> {
            // 初始化一条流水表
            String stockLogId = promoService.insertNewStockLog(promoId, amount);
            // 进行事务处理
            boolean flag = promoService.insertNewPromoOrder(promoId, amount, userId, stockLogId);
            if (flag) {
                jedis.del(tokenKey);
//                return PromoOrderRespVo.ok();
            } else {
                log.info("下单失败！promoId:{},amount:{},userId:{}",promoId,amount,userId);
                throw new GunsException(GunsExceptionEnum.DATABASE_ERROR);
//                return PromoOrderRespVo.fail();
            }
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException | GunsException e) {
            e.printStackTrace();
            return PromoOrderRespVo.fail();
        }
        return PromoOrderRespVo.ok();
    }

    @GetMapping("/publishPromoStock")
    public PromoStockRespVo publishPromoStock(@RequestParam(required = false, name = "cinemaId") Integer cinemaId) {
        // 判断之前是否刷新过缓存
        if (jedis.exists(CachePromoPrefix.PROMO_STOCK_CACHE_PREFIX)) {
            return PromoStockRespVo.success("已经刷新过了");
        }
        PromoStockRespVo stockRespVo = promoService.publishPromoStock(cinemaId);
        jedis.set(CachePromoPrefix.PROMO_STOCK_CACHE_PREFIX, "success");
        jedis.expire(CachePromoPrefix.PROMO_STOCK_CACHE_PREFIX, 1800);
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
