package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.order.OrderService;
import com.stylefeng.guns.rest.order.vo.OrderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {
    @Autowired
    MoocOrderTMapper orderTMapper;

    @Override
    public boolean isTrueSeats(int fieldId, String seats) {
        return false;
    }

    @Override
    public boolean isNotSoldSeats(int fieldId, String seats) {
        return false;
    }

    @Override
    public OrderData saveOrderInfo(int fieldId, String soldSeats, String seatsName, int userId) {
        return null;
    }
}
