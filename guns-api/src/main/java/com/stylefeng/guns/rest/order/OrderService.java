package com.stylefeng.guns.rest.order;

import com.stylefeng.guns.rest.order.vo.BuyTicketsVo;
import com.stylefeng.guns.rest.order.vo.OrderRespVo;

public interface OrderService {
    OrderRespVo buyTickets(int userId, BuyTicketsVo buyTicketsVo);
}
