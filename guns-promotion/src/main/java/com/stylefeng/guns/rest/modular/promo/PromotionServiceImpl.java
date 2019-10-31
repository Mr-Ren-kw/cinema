package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.common.enums.PromoStatus;
import com.stylefeng.guns.rest.common.enums.StockLogStatus;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoOrderMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeStockLogMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromo;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoOrder;
import com.stylefeng.guns.rest.common.persistence.model.MtimeStockLog;
import com.stylefeng.guns.rest.config.properties.RedisProperties;
import com.stylefeng.guns.rest.modular.promo.util.DateUtil;
import com.stylefeng.guns.rest.modular.promo.util.UuidUtil;
import com.stylefeng.guns.rest.promo.CachePrefix;
import com.stylefeng.guns.rest.promo.PromotionService;
import com.stylefeng.guns.rest.promo.vo.PromoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import static com.stylefeng.guns.rest.modular.promo.util.UuidUtil.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@Service(interfaceClass = PromotionService.class)
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    MtimePromoMapper mtimePromoMapper;

    @Autowired
    MtimePromoStockMapper mtimePromoStockMapper;

    @Autowired
    MtimePromoOrderMapper mtimePromoOrderMapper;

    @Reference(interfaceClass = CinemaService.class)
    CinemaService cinemaService;

    @Autowired
    Jedis jedis;

    @Autowired
    RedisProperties redisProperties;

    @Autowired
    MtimeStockLogMapper stockLogMapper;

    @Autowired
    RocketMQProducer producer;

    @Override
    public PromoVO[] queryAllPromotions() {
        MtimePromo[] mtimePromos = mtimePromoMapper.queryAllPromos();
        if (mtimePromos == null) {
            return null;
        }
        List<PromoVO> promoVOList = new ArrayList<>();
        for (MtimePromo mtimePromo : mtimePromos) {
            Integer cinemaId = mtimePromo.getCinemaId();
            String cinemaName = cinemaService.getCinemaNameById(cinemaId);
            String cinemaAddress = cinemaService.getCinemaAddressById(cinemaId);
            String imgAddress = cinemaService.getImgAddressById(cinemaId);
            Integer stock = mtimePromoStockMapper.queryStockByPid(mtimePromo.getUuid());
            PromoVO promoVO = new PromoVO(cinemaAddress, cinemaId, cinemaName, mtimePromo.getDescription(), mtimePromo.getEndTime(), imgAddress, mtimePromo.getPrice().intValue(), mtimePromo.getStartTime(), mtimePromo.getStatus(), stock, mtimePromo.getUuid());
            promoVOList.add(promoVO);
        }
        PromoVO[] res = new PromoVO[promoVOList.size()];
        return promoVOList.toArray(res);
    }

    @Override
    public PromoVO[] queryPromotionsByCinemaId(Integer cinemaId) {
        MtimePromo[] mtimePromos = mtimePromoMapper.queryPromosByCid(cinemaId);
        if (mtimePromos == null) {
            return null;
        }
        List<PromoVO> promoVOList = new ArrayList<>();
        for (MtimePromo mtimePromo : mtimePromos) {
            String cinemaName = cinemaService.getCinemaNameById(cinemaId);
            String cinemaAddress = cinemaService.getCinemaAddressById(cinemaId);
            String imgAddress = cinemaService.getImgAddressById(cinemaId);
            Integer stock = mtimePromoStockMapper.queryStockByPid(mtimePromo.getUuid());
            PromoVO promoVO = new PromoVO(cinemaAddress, cinemaId, cinemaName, mtimePromo.getDescription(), mtimePromo.getEndTime(), imgAddress, mtimePromo.getPrice().intValue(), mtimePromo.getStartTime(), mtimePromo.getStatus(), stock, mtimePromo.getUuid());
            promoVOList.add(promoVO);
        }
        PromoVO[] res = new PromoVO[promoVOList.size()];
        return promoVOList.toArray(res);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean createOrder(int userId, Integer promoId, Integer amount, String stockLogId) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        //验证promId， amount, time
        // 这里还有一种思路是, 看redis中stock_log有没有，如果有，则stock存在，否则调用updatePromoStock
        // 但是可能有线程一边在del　key, 有线程在请求updatePromoStock这个接口
        // 这里没做参数校验
        Integer stock;
        // redis 获得库存
        String s = jedis.get(redisProperties.getPrefix() + promoId);
        if (s == null) {
            // 说明stock不在redis里
            stock = mtimePromoStockMapper.queryStockByPid(promoId);
            jedis.set(redisProperties.getPrefix() + promoId, stock + "");
            jedis.expire(redisProperties.getPrefix() + promoId, redisProperties.getExpiration());
        } else {
            stock = Integer.parseInt(s);
        }
        if (stock < amount) {
            // 没有库存
            return false;
        } else {
            // redis减去库存
            jedis.incrBy(redisProperties.getPrefix() + promoId, -amount);
            // 删除这个标志，会导致下次请求updatePromoStock这个接口时重新从数据库取数据
            jedis.del(redisProperties.getPrefix() + redisProperties.getLog());
            stock = stock - amount;
        }
        Date now = new Date();
        MtimePromo mtimePromo = mtimePromoMapper.selectById(promoId);
        if(mtimePromo == null) {
            stockLogMapper.updateStatusById(stockLogId, StockLogStatus.FAIL.getIndex());
            return false;
        }
        if (mtimePromo.getStartTime().after(now) || mtimePromo.getEndTime().before(now)) {
            // 活动未开始或者已过期
            return false;
        }
//        StockLogVO stockLogVO = new StockLogVO();
//        stockLogVO.setUserId(userId);
//        stockLogVO.setAmount(amount);
//        stockLogVO.setPromoId(promoId);
//        stockLogVO.setStockLogId(stockLogId);
        MtimePromoOrder mtimePromoOrder = new MtimePromoOrder();
        String uuid = getUUID();
        mtimePromoOrder.setUuid(uuid);
        mtimePromoOrder.setAmount(amount);
        mtimePromoOrder.setUserId(userId);
        mtimePromoOrder.setPrice(mtimePromo.getPrice().multiply(new BigDecimal(amount)));
        mtimePromoOrder.setCinemaId(mtimePromo.getCinemaId());
        // 这里的开始时间和结束时间是兑换码的开始和结束时间, 开始为now, 结束为3个月后
        mtimePromoOrder.setStartTime(now);
        mtimePromoOrder.setEndTime(DateUtil.addNow(3));
        mtimePromoOrder.setExchangeCode("EX" + uuid);
        mtimePromoOrder.setCreateTime(now);
        // 创建订单
        Integer insert = mtimePromoOrderMapper.insert(mtimePromoOrder);
        if(insert < 1) {
            // 修改库存流水表的status字段为失败
            stockLogMapper.updateStatusById(stockLogId,StockLogStatus.FAIL.getIndex());
            return false;
        }

        return true;
    }

    @Override
    public boolean updatePromoStock(Integer cinemaId) {
        String s = jedis.get(redisProperties.getPrefix() + redisProperties.getLog());
        if (s == null) {
            // 说明没有这个key
            PromoVO[] promoVOS = mtimePromoMapper.queryPromoByCidStatus(cinemaId, PromoStatus.VALID.getStatus());
            if (promoVOS != null) {
                for (PromoVO promoVO : promoVOS) {
                    Integer uuid = promoVO.getUuid();
                    String key = redisProperties.getPrefix() + uuid;
                    jedis.set(key, promoVO.getStock() + "");
                    jedis.expire(redisProperties.getPrefix() + promoVO.getUuid(), redisProperties.getExpiration());

                    String promoAmountKey = CachePrefix.PROMO_TOKEN_LIMIT + promoVO.getUuid();
                    if(null == jedis.get(promoAmountKey)) {
                        // token　没初始化则初始化
                        jedis.set(promoAmountKey, promoVO.getStock() * redisProperties.getTokenFactor() + "");
                    }
                }
            }
            jedis.set(redisProperties.getPrefix() + redisProperties.getLog(), "true");
        }
        return true;
    }

    @Override
    public Boolean transactionalCreateOrder(int userId, Integer promoId, Integer amount, String stockLogId) {
        return producer.transactionCreateOrder(userId, promoId, amount, stockLogId);
    }

    @Override
    public String initPromoStockLog(Integer promoId, Integer amount) {
        MtimeStockLog mtimeStockLog = new MtimeStockLog();
        mtimeStockLog.setPromoId(promoId);
        mtimeStockLog.setAmount(amount);
        String uuid = UuidUtil.getUUID();
        mtimeStockLog.setUuid(uuid);
        // 初始化时，设置状态
        mtimeStockLog.setStatus(StockLogStatus.UNKNOWN.getIndex());
        Integer insert = stockLogMapper.insert(mtimeStockLog);
        if(insert > 0) {
            return uuid;
        }else {
            return null;
        }
    }

    @Override
    public String generateToken(int userId, Integer promoId) {
        String uuid = getUUID();
        String promoAmountKey = CachePrefix.PROMO_TOKEN_LIMIT + promoId;
        Long remain = jedis.incrBy(promoAmountKey, -1);
        if(remain < 0) {
            log.info("秒杀令牌已经发放完！promoId:{}",promoId);
            return null;
        }

        String key = CachePrefix.PROMO_STOKE_TOKEN_USERID + userId + "_PROMOID_" + promoId;
        jedis.set(key, uuid);
        return uuid;
    }
}
