package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.modular.promo.vo.BasePromoResponseVO;
import com.stylefeng.guns.rest.promo.PromoService;
import com.stylefeng.guns.rest.promo.vo.GetPromoByIdVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/19 14:46
 */
@RestController
@RequestMapping("/promo")
public class PromoController {

    @Reference(interfaceClass = PromoService.class,check = false)
    PromoService promoService;

    /**
     * 根据影院id查询秒杀订单列表
     * @param cinemaId 当不传入的时候查询所有的秒杀活动列表
     * @return
     */
    @RequestMapping("/getPromo")
    public BasePromoResponseVO getPromo(Integer cinemaId){
        List<GetPromoByIdVO> promoByCinemaId = promoService.getPromoByCinemaId(cinemaId);
        BasePromoResponseVO basePromoResponseVO = new BasePromoResponseVO();
        basePromoResponseVO.setData(promoByCinemaId);
        basePromoResponseVO.setStatus(0);
        return basePromoResponseVO;
    }
}
