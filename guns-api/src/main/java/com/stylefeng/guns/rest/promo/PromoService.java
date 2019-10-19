package com.stylefeng.guns.rest.promo;

import com.stylefeng.guns.rest.promo.vo.GetPromoByIdVO;

import java.util.List;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/19 14:47
 */
public interface PromoService {

    List<GetPromoByIdVO> getPromoByCinemaId(Integer cinemaId);

}
