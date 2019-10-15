package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.order.OrderService;
import com.stylefeng.guns.rest.order.vo.BuyTicketsVo;
import com.stylefeng.guns.rest.order.vo.OrderRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Reference(interfaceClass = OrderService.class)
    private OrderService orderService;

    @RequestMapping("/buyTickets")
    public OrderRespVo buyTickets(BuyTicketsVo buyTicketsVo, HttpServletRequest request) {
        int userId = jwtTokenUtil.parseToken(request);
        return orderService.buyTickets(userId,buyTicketsVo);
    }
}
