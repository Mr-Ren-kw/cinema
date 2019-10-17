package com.stylefeng.guns.rest.order;

import com.stylefeng.guns.rest.order.vo.OrderData;
import com.stylefeng.guns.rest.order.vo.OrderInfo;
import com.stylefeng.guns.rest.order.vo.OrderPage;

import java.util.List;

public interface OrderService {
    // 验证要售出的票是否为真
    boolean isTrueSeats(int fieldId,String seats);

    // 查看已经销售的座位里，有没有这些座位
    boolean isNotSoldSeats(int fieldId, String seats);

    // 创建订单信息
    OrderData saveOrderInfo(int fieldId, String soldSeats, String seatsName, int userId);

    //获得用户所有订单信息
    List<OrderInfo> getOrderByUserId(int userId, OrderPage page);
}
