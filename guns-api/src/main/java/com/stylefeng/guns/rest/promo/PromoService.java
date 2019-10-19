package com.stylefeng.guns.rest.promo;


import com.stylefeng.guns.rest.promo.vo.PromoOrderVo;
import com.stylefeng.guns.rest.promo.vo.PromoRespVo;

public interface PromoService {
    // 通过cinemaId查询秒杀活动信息，如果cinemaId为空，则查询所有
    PromoRespVo getPromoList(Integer cinemaId);
    // 创建新的秒杀活动订单
    PromoRespVo createPromoOrder(PromoOrderVo promoOrderVo,int userId);

}
