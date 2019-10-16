package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.order.OrderService;
import com.stylefeng.guns.rest.order.vo.OrderData;
import com.stylefeng.guns.rest.order.vo.OrderResultResponseVO;
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

    @Override
    public OrderResultResponseVO getPayResult(Integer orderId) {
        MoocOrderT moocOrderT = orderTMapper.selectById(orderId);
        OrderResultResponseVO orderResultResponseVO = new OrderResultResponseVO();
        orderResultResponseVO.setOrderId(moocOrderT.getUuid());
        orderResultResponseVO.setOrderStatus(moocOrderT.getOrderStatus());
        String msg;
        if (moocOrderT.getOrderStatus() == 1){
            msg = "支付成功";
        }else {
            msg = "支付失败";
        }
        orderResultResponseVO.setOrderMsg(msg);
        return orderResultResponseVO;
    }
}
