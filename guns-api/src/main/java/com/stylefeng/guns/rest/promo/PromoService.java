package com.stylefeng.guns.rest.promo;

import com.stylefeng.guns.rest.promo.vo.PromoRespVo;
import com.stylefeng.guns.rest.promo.vo.PromoStockRespVo;

public interface PromoService {
    // 通过cinemaId查询秒杀活动信息，如果cinemaId为空，则查询所有
    PromoRespVo getPromoList(Integer cinemaId);
    // 执行本地事务，创建新的秒杀活动订单
    boolean createPromoOrder(int promoId, int amount, int userId, String stockLogId);
    // 创建一条新的流水表条目
    String insertNewStockLog(int promoId, int amount);
    // controller调用的下单接口
    boolean insertNewPromoOrder(int promoId, int amount, int userId, String stockLogId);
    // 将库存信息发布到缓存
    PromoStockRespVo publishPromoStock(Integer cinemaId);
    // 获取秒杀令牌
    String generateToken(int promoId, int userId);
}
