package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.order.vo.*;
import com.stylefeng.guns.rest.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Reference(interfaceClass = OrderService.class, check = false)
    private OrderService orderService;

    @RequestMapping("/buyTickets")
    public OrderRespVo buyTickets(BuyTicketsVo buyTicketsVo, HttpServletRequest request) {
        int userId = jwtTokenUtil.parseToken(request);
        int fieldId = buyTicketsVo.getFieldId();
        String soldSeats = buyTicketsVo.getSoldSeats();
        OrderRespVo<OrderData> orderRespVo = new OrderRespVo<>();
        // 先判断要出售的票是否为真
        boolean flag = orderService.isTrueSeats(fieldId, soldSeats);
        if (!flag) {
            return OrderRespVo.fail(orderRespVo);
        }
        // 判断座位是否已被售出
        flag = orderService.isNotSoldSeats(fieldId,soldSeats);
        if (!flag) {
            return OrderRespVo.fail(orderRespVo);
        }
        // 出售座位，创建订单信息
        OrderData orderData = orderService.saveOrderInfo(fieldId, soldSeats, buyTicketsVo.getSeatsName(), userId);
        orderRespVo.setData(orderData);
        return orderRespVo;
    }

    @RequestMapping("/getPayResult")
    public OrderRespVo getPayResult(@RequestBody OrderResultVO orderResultVO) {
        Integer tryNums = orderResultVO.getTryNums();
        if (tryNums < 4) {
            // 没超时
            OrderResultResponseVO payResult = orderService.getPayResult(orderResultVO.getOrderId());
            if (payResult.getOrderStatus() == 1) {
                // 查询到订单状态是1，支付成功
                OrderRespVo orderRespVo = new OrderRespVo();
                orderRespVo.setData(payResult);
                orderRespVo.setStatus(1);
                orderRespVo.setMsg("支付成功");
                return orderRespVo;
            }
        }
        OrderRespVo orderRespVo = new OrderRespVo();
        orderRespVo.setStatus(1);
        orderRespVo.setMsg("订单支付失败，请稍后重试");
        return orderRespVo;
    }

    @RequestMapping("/getOrderInfo")
    public OrderRespVo grtAllOrderInfo(OrderPage page, HttpServletRequest httpServletRequest) {
        OrderRespVo<List<OrderInfo>> response = new OrderRespVo<>();
        int userId = jwtTokenUtil.parseToken(httpServletRequest);
        List<OrderInfo> data = orderService.getOrderByUserId(userId, page);
        response.setData(data);
        return response;
    }
}
