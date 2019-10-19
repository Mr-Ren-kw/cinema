package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.rest.persistence.dao.MtimePromoMapper;
import com.stylefeng.guns.rest.persistence.dao.MtimePromoOrderMapper;
import com.stylefeng.guns.rest.persistence.dao.MtimePromoStockMapper;
import com.stylefeng.guns.rest.persistence.model.MtimePromo;
import com.stylefeng.guns.rest.promo.PromoService;
import com.stylefeng.guns.rest.promo.vo.GetPromoByIdVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/19 15:55
 */
@Component
@Service(interfaceClass =  PromoService.class)
public class PromoServiceImpl implements PromoService {
    @Autowired
    MtimePromoMapper mtimePromoMapper;
    @Autowired
    MtimePromoOrderMapper mtimePromoOrderMapper;
    @Autowired
    MtimePromoStockMapper mtimePromoStockMapper;

    @Override
    public List<GetPromoByIdVO> getPromoByCinemaId(Integer cinemaId) {
        EntityWrapper<MtimePromo> entityWrapper = new EntityWrapper<>();
        if (cinemaId != null){
            entityWrapper.eq("cinema_id",cinemaId);
        }
        List<MtimePromo> mtimePromos = mtimePromoMapper.selectList(entityWrapper);
        List<GetPromoByIdVO> getPromoByIdVOList = new ArrayList<>();
        for (MtimePromo mtimePromo : mtimePromos) {

        }

        return getPromoByIdVOList;
    }
}
