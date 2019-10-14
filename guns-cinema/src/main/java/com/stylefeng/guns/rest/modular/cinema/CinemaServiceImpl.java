package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeAreaDictTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeBrandDictTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeHallDictTMapper;
import com.stylefeng.guns.rest.common.persistence.model.codition.Area;
import com.stylefeng.guns.rest.common.persistence.model.codition.Brand;
import com.stylefeng.guns.rest.common.persistence.model.codition.CoditionData;
import com.stylefeng.guns.rest.common.persistence.model.codition.HallType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Service(interfaceClass = CinemaService.class)
public class CinemaServiceImpl implements CinemaService {

    @Autowired
    MtimeAreaDictTMapper areaDictTMapper;

    @Autowired
    MtimeBrandDictTMapper brandDictTMapper;

    @Autowired
    MtimeHallDictTMapper hallDictTMapper;

    @Override
    public Object getCodition(int brandId, int hallType, int areaId) {
        CoditionData coditionData = new CoditionData();
        // 查询area
        List<Area> areaList = areaDictTMapper.getAreaList();
        for (Area area : areaList) {
            if (area.getAreaId() == areaId) {
                area.setActive(true);
            }
        }
        coditionData.setAreaList(areaList);
        // 查询brand
        List<Brand> brandList = brandDictTMapper.getBrandList();
        for (Brand brand : brandList) {
            if (brand.getBrandId() == brandId) {
                brand.setActive(true);
            }
        }
        coditionData.setBrandList(brandList);
        // 查询hall_type
        List<HallType> hallTypeList = hallDictTMapper.getHallList();
        for (HallType type : hallTypeList) {
            if (type.getHalltypeId() == hallType) {
                type.setActive(true);
            }
        }
        coditionData.setHalltypeList(hallTypeList);
        return coditionData;
    }
}
