package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.core.constant.StockLogStatus;
import com.stylefeng.guns.core.exception.GunsException;
import com.stylefeng.guns.core.exception.GunsExceptionEnum;
import com.stylefeng.guns.core.util.UuidUtil;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.cinema.vo.CinemaMsgForPromo;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoOrderMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeStockLogMapper;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.modular.mq.MqProducer;
import com.stylefeng.guns.rest.promo.PromoService;
import com.stylefeng.guns.rest.promo.consistent.CachePromoPrefix;
import com.stylefeng.guns.rest.promo.vo.PromoRespVo;
import com.stylefeng.guns.rest.promo.vo.PromoStockRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Component
@Slf4j
@Service(interfaceClass = PromoService.class)
public class PromoServiceImpl implements PromoService {

    @Autowired
    MtimePromoMapper promoMapper;

    @Autowired
    MtimePromoStockMapper promoStockMapper;

    @Autowired
    MtimePromoOrderMapper promoOrderMapper;

    @Autowired
    MtimeStockLogMapper stockLogMapper;

    @Reference(interfaceClass = CinemaService.class,check = false)
    CinemaService cinemaService;

    @Autowired
    Jedis jedis;

    @Autowired
    MqProducer producer;

    @Override
    public PromoRespVo<List<PromoData>> getPromoList(Integer cinemaId) {
        List<PromoData> promoDataList = new LinkedList<>();
        // 先查询promo表
        List<MtimePromo> promoList = promoMapper.selectPromoByCinemaId(cinemaId);
        CinemaMsgForPromo cinemaMsg;
        for (MtimePromo mtimePromo : promoList) {
            PromoData promo = new PromoData();
            promo.setCinemaId(mtimePromo.getCinemaId());
            promo.setDescription(mtimePromo.getDescription());
            promo.setEndTime(mtimePromo.getEndTime());
            promo.setPrice(mtimePromo.getPrice().intValue());
            promo.setStartTime(mtimePromo.getStartTime());
            promo.setStatus(mtimePromo.getStatus());
            promo.setUuid(mtimePromo.getUuid());
            // 根据cinemaId查询cinema表进行参数封装
            cinemaMsg = cinemaService.getCinemaMsgForPromoById(mtimePromo.getCinemaId());
            promo.setCinemaAddress(cinemaMsg.getCinemaAddress());
            promo.setCinemaName(cinemaMsg.getCinemaName());
            promo.setImgAddress(cinemaMsg.getImgAddress());
            // 根据秒杀活动id查询stock表获取库存
            promo.setStock(promoStockMapper.selectStockByPromoId(promo.getUuid()));
            promoDataList.add(promo);
        }
        PromoRespVo<List<PromoData>> listPromoRespVo = new PromoRespVo<>();
        listPromoRespVo.setData(promoDataList);
        listPromoRespVo.setStatus(0);
        listPromoRespVo.setMsg("成功");
        return listPromoRespVo;
    }

    /**
     * 初始化一条流水表条目
     * @param promoId 活动id
     * @param amount 购买数量
     * @return 新插入的条目的uuid
     */
    @Override
    public String insertNewStockLog(int promoId, int amount) {
        MtimeStockLog stockLog = new MtimeStockLog();
        String uuid = UuidUtil.getUuidOf16();
        stockLog.setUuid(uuid);
        stockLog.setAmount(amount);
        stockLog.setPromoId(promoId);
        stockLog.setStatus(StockLogStatus.UNKNOWN.getIndex());
        stockLogMapper.insert(stockLog);
        return uuid;
    }

    /**
     * 调用producer提供的事务处理进行秒杀下单
     * @param promoId
     * @param amount
     * @param userId
     * @return
     */
    @Override
    public boolean insertNewPromoOrder(int promoId, int amount, int userId, String stockLogId) {
        return producer.transactionCreateOrder(promoId,userId,amount,stockLogId);
    }

    /**
     * 将库存信息刷新到缓存
     * @param cinemaId
     * @return
     */
    @Override
    public PromoStockRespVo publishPromoStock(Integer cinemaId) {
        List<MtimePromo> promoList = promoMapper.selectPromoByCinemaId(cinemaId);
        for (MtimePromo mtimePromo : promoList) {
            // 将库存信息放到redis缓存中
            Integer promoUuid = mtimePromo.getUuid();
            Integer stock = promoStockMapper.selectStockByPromoId(promoUuid);
            jedis.set(CachePromoPrefix.PROMO_STOCK_CACHE_PREFIX + promoUuid, String.valueOf(stock));
            // 根据库存信息生成秒杀令牌的数量
            String tokenAmountKey = CachePromoPrefix.PROMO_TOKEN_PREFIX + promoUuid;
            String tokenAmount = String.valueOf(stock * 5);
            jedis.set(tokenAmountKey, tokenAmount);
        }
        return PromoStockRespVo.success("发布成功");
    }

    @Override
    public String generateToken(int promoId, int userId) {
        String token = UuidUtil.getUuidOf16();
        // 扣减缓存中对应的token数量
        Long result = jedis.decr(CachePromoPrefix.PROMO_TOKEN_PREFIX + promoId);
        if (result == 0) {
            jedis.set(CachePromoPrefix.PROMO_TOKEN_EMPTY_PREFIX + promoId, "empty");
            log.info("秒杀活动令牌已发完！promoId:{}", promoId);
        }
        // 将生成的token存到redis中
        String tokenKey = CachePromoPrefix.PROMO_TOKEN_PREFIX + "userId_" + userId + "_promoId_" + promoId;
        jedis.set(tokenKey,token);
        return token;
    }

    /**
     * 创建订单，并修改流水表的状态，修改redis缓存中的库存
     * @param promoId
     * @param amount
     * @param userId
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean createPromoOrder(int promoId, int amount, int userId, String stockLogId) {
        // 先判断是否还有余票
        // 校验参数
        if (amount <= 0) {
            return false;
        }
        // 先查询redis，没有查询到再查询promo_stock表
        String stockStr = jedis.get(CachePromoPrefix.PROMO_STOCK_CACHE_PREFIX + promoId);
        int stock;
        if (stockStr == null) {
            stock = promoStockMapper.selectStockByPromoId(promoId);
            jedis.set(CachePromoPrefix.PROMO_STOCK_CACHE_PREFIX + promoId, String.valueOf(stock));
        }else {
            stock = Integer.parseInt(stockStr);
        }
        // 判断库存是否足够
        if (stock < amount) {
            return false;
        }
        // 进行本地事务处理
        // 1.添加订单
        // 先查询秒杀活动信息
        MtimePromo promo = promoMapper.selectById(promoId);
        // 生成新的订单对象
        MtimePromoOrder promoOrder = buildPromoOrder(promo,amount,userId);
        // 插入到数据库中
        Integer insert = promoOrderMapper.insert(promoOrder);
        if (insert != 1) {
            // 添加订单失败
            // 将流水表中对应的条目状态改为失败
            stockLogMapper.updateStockStatusById(stockLogId, StockLogStatus.FAIL.getIndex());
            throw new GunsException(GunsExceptionEnum.DATABASE_ERROR);
        }
        // 插入成功，开始修改redis中的库存
        boolean decrease = decreaseRedisStock(promoId, amount);
        if (!decrease) {
            // 将流水表中对应的条目状态改为失败
            stockLogMapper.updateStockStatusById(stockLogId, StockLogStatus.FAIL.getIndex());
            throw new GunsException(GunsExceptionEnum.STOCK_ERROR);
        }
        // 下单成功，将流水表中的条目状态改为成功
        stockLogMapper.updateStockStatusById(stockLogId, StockLogStatus.SUCCESS.getIndex());
        return true;
    }

    /**
     * 新建一个订单对象
     * @param promo 秒杀活动的信息
     * @param amount 购买数量
     * @param userId 用户Id
     * @return 生成的新的订单对象
     */
    private MtimePromoOrder buildPromoOrder(MtimePromo promo, int amount, int userId) {
        MtimePromoOrder promoOrder = new MtimePromoOrder();
        String orderId = UuidUtil.getUuidOf16();
        String exchangeCode = UuidUtil.getUuidOf16();
        promoOrder.setUuid(orderId);
        promoOrder.setUserId(userId);
        promoOrder.setCinemaId(promo.getCinemaId());
        promoOrder.setExchangeCode(exchangeCode);
        promoOrder.setAmount(amount);
        // 计算金额需要用BigDecimal
        BigDecimal price = promo.getPrice();
        BigDecimal orderPrice = price.multiply(new BigDecimal(amount)).setScale(2, RoundingMode.HALF_UP);
        promoOrder.setPrice(orderPrice);
        promoOrder.setStartTime(promo.getStartTime());
        promoOrder.setEndTime(promo.getEndTime());
        promoOrder.setCreateTime(new Date());
        return promoOrder;
    }

    /**
     * 修改redis中的库存，如果结果不合法，再将库存还回去
     * @param promoId 对应的秒杀活动id
     * @param amount 要减去的数量
     * @return 判断减去之后的结果是否合法
     */
    private boolean decreaseRedisStock(int promoId, int amount) {
        String key = CachePromoPrefix.PROMO_STOCK_CACHE_PREFIX + promoId;
        Long result = jedis.decrBy(key, amount);
        if (result == 0) {
            jedis.set(CachePromoPrefix.PROMO_STOCK_EMPTY_PREFIX + promoId, "empty");
            log.info("秒杀活动已发完！promoId:{}", promoId);
        }
        if (result < 0) {
            log.info("库存不足！promoId:{}", promoId);
            jedis.incrBy(key, amount);
            return false;
        }
        return true;
    }
}
