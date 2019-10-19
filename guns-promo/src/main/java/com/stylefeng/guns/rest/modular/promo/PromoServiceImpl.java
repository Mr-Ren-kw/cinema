package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.cinema.vo.CinemaMsgForPromo;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoOrderMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromo;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoOrder;
import com.stylefeng.guns.rest.common.persistence.model.PromoData;
import com.stylefeng.guns.rest.common.persistence.model.PromoOrderRespData;
import com.stylefeng.guns.rest.promo.PromoService;
import com.stylefeng.guns.rest.promo.vo.PromoRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Component
@Service(interfaceClass = PromoService.class)
public class PromoServiceImpl implements PromoService {

    @Autowired
    MtimePromoMapper promoMapper;

    @Autowired
    MtimePromoStockMapper promoStockMapper;

    @Autowired
    MtimePromoOrderMapper promoOrderMapper;

    @Reference(interfaceClass = CinemaService.class)
    CinemaService cinemaService;

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
        return listPromoRespVo;
    }

    @Override
    public PromoRespVo createPromoOrder(com.stylefeng.guns.rest.promo.vo.PromoOrderVo promoOrderVo, int userId) {
        PromoRespVo<PromoOrderRespData> promoRespVo = new PromoRespVo<>();
        PromoOrderRespData promoOrderData = new PromoOrderRespData();
        // 先判断是否还有余票
        int promoId = promoOrderVo.getPromoId();
        int amount = promoOrderVo.getAmount();
        // 查询promo_stock表
        int stock = promoStockMapper.selectStockByPromoId(promoId);
        if (stock < amount) {
            promoRespVo.setData(promoOrderData.fail());
            return promoRespVo.fail(402,"下单失败");
        }
        // 修改库存
        int result = promoStockMapper.updateStockByPromoId(promoId, stock - amount);
        if (result != 1) {
            promoRespVo.setData(promoOrderData.fail());
            return promoRespVo.fail(402,"下单失败");
        }
        // 添加订单
        MtimePromo promo = promoMapper.selectById(promoId);
        MtimePromoOrder promoOrder = new MtimePromoOrder();
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        String orderId = uuid.substring(0, 16);
        String exchangeCode = uuid.substring(uuid.length() - 16);
        promoOrder.setUuid(orderId);
        promoOrder.setUserId(userId);
        promoOrder.setCinemaId(promo.getCinemaId());
        promoOrder.setExchangeCode(exchangeCode);
        promoOrder.setAmount(amount);
        promoOrder.setPrice(new BigDecimal(amount * promo.getPrice().intValue()));
        promoOrder.setStartTime(promo.getStartTime());
        promoOrder.setEndTime(promo.getEndTime());
        promoOrder.setCreateTime(new Date());
        Integer insert = promoOrderMapper.insert(promoOrder);
        if (insert == 1) {
            // 下单成功
            promoOrderData.setStatus("0");
            promoOrderData.setMsg("下单成功");
            return promoRespVo.ok(promoOrderData);
        }
        promoRespVo.setData(promoOrderData.fail());
        return promoRespVo.fail(402,"下单失败");
    }
}
