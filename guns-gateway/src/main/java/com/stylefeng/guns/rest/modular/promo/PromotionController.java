package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.core.exception.GunsException;
import com.stylefeng.guns.core.exception.GunsExceptionEnum;
import com.stylefeng.guns.core.exception.ServiceExceptionEnum;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.promo.CachePrefix;
import com.stylefeng.guns.rest.promo.PromotionService;
import com.stylefeng.guns.rest.promo.vo.PromoRespVO;
import com.stylefeng.guns.rest.promo.vo.PromoVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.*;

@RestController
@RequestMapping("/promo")
public class PromotionController {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Reference(interfaceClass = PromotionService.class, check = false)
    private PromotionService promotionService;

    @Autowired
    Jedis jedis;

    private ThreadPoolExecutor threadPoolExecutor;

    @PostConstruct
    public void init() {
        // 200 线程 100个等待队列
        threadPoolExecutor = new ThreadPoolExecutor(200, 400, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100), Executors.defaultThreadFactory());
    }

    @PreDestroy
    public void close() {
        threadPoolExecutor.shutdown();
    }

    @RequestMapping("getPromo")
    public PromoRespVO getPromo(Integer cinemaId) {
        PromoRespVO promoRespVO = new PromoRespVO();
        PromoVO[] promos = new PromoVO[0];
        int status = 0;
        String msg = "ok";
        try {
            if (cinemaId == null) {
                // 查询所有
                promos = promotionService.queryAllPromotions();
            } else {
                // 根据cinemaId查询
                promos = promotionService.queryPromotionsByCinemaId(cinemaId);
            }
        } catch (Exception e) {
            status = 999;
            msg = "服务器发生异常，请联系管理员";
            promoRespVO.setMsg(msg);
            promoRespVO.setStatus(status);
            return promoRespVO;
        }
        // 保证传回去一个非null数组
        promos = promos == null ? new PromoVO[0] : promos;
        promoRespVO.setData(promos);
        promoRespVO.setMsg(msg);
        promoRespVO.setStatus(status);
        return promoRespVO;
    }

    @RequestMapping("createOrder")
    public PromoRespVO createOrder(HttpServletRequest request,
                                   @RequestParam(required = true) Integer promoId,
                                   @RequestParam(required = true) Integer amount,
                                   @RequestParam(required = true) String promoToken) {
        // 需要考虑活动过期的情况，就是查询的时候他还没过期，下订单的时候过期了
        int userId = jwtTokenUtil.parseToken(request);
        PromoRespVO promoRespVO = new PromoRespVO();
        String msg = "OK";
        Integer status = 0;
        if (promoId == null || amount == null) {
            status = 1;
            msg = "下订单失败，请重试";
        }
        // 校验秒杀令牌
        String promoTokenKey = CachePrefix.PROMO_STOKE_TOKEN_USERID + userId + "_PROMOID_" + promoId;
        String promoTokenFromCache = jedis.get(promoTokenKey);
        if (null == promoTokenFromCache || !promoTokenFromCache.equals(promoToken)) {
            return new PromoRespVO(1, "令牌校验失败 !");
        }

        Future future = threadPoolExecutor.submit(() -> {
            boolean res = true;
            final String stockLogId = promotionService.initPromoStockLog(promoId, amount);
            if(StringUtils.isBlank(stockLogId)) {
                throw new GunsException(GunsExceptionEnum.STOCK_LOG_INIT_ERROR);
            }
            try {
                res = promotionService.transactionalCreateOrder(userId, promoId, amount, stockLogId);
            } catch (Exception e) {
                e.printStackTrace();
                throw new GunsException(GunsExceptionEnum.DATABASE_ERROR);
            }
            if (!res) {
                throw new GunsException(GunsExceptionEnum.DATABASE_ERROR);
            }
        });
        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new PromoRespVO(1, "下单失败!");
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new PromoRespVO(1, "下单失败!");
        } catch (GunsException e) {
            e.printStackTrace();
            return new PromoRespVO(1, "下单失败!");
        }
        promoRespVO.setMsg(msg);
        promoRespVO.setStatus(status);
        return promoRespVO;
    }

    /**
     * 将库存信息加载到缓存redis中, 加上一个log, 判断是否是反复的请求该接口，在更新库存的时候就删除该log
     *
     * @return {
     * "status":"0",
     * "msg":"发布成功"
     * }
     */
    @RequestMapping("publishPromoStock")
    public PromoRespVO publishPromoStock(Integer cinemaId) {
        boolean res = false;
        try {
            res = promotionService.updatePromoStock(cinemaId);
        } catch (Exception e) {
            e.printStackTrace();
            return new PromoRespVO(999, "服务器异常！");
        }
        return new PromoRespVO(0, "发布成功");
    }

    @RequestMapping("generateToken")
    public PromoRespVO generateToken(@RequestParam(required = true) Integer promoId, HttpServletRequest request) {
        int userId = jwtTokenUtil.parseToken(request);

        // 库存售罄key, 用于判断库存是否售罄
        String key = CachePrefix.PROMO_STOCK_SOLDOUT_PROMOID + promoId;
        String s = jedis.get(key);
        if (s != null) {
            // 该标示不为空
            return new PromoRespVO(1, "库存已售罄！");
        }


        // 生成秒杀令牌
        String token = promotionService.generateToken(userId, promoId);
        if (StringUtils.isBlank(token)) {
            return new PromoRespVO(999, "服务器异常！");
        }
        return new PromoRespVO(0, token);
    }
}
