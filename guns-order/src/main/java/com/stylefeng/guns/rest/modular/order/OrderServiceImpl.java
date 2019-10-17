package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.film.FilmService;
import com.stylefeng.guns.rest.order.OrderService;
import com.stylefeng.guns.rest.order.vo.OrderData;
import com.stylefeng.guns.rest.order.vo.OrderInfo;
import com.stylefeng.guns.rest.order.vo.OrderPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {
    @Autowired
    MoocOrderTMapper orderTMapper;
    @Reference(interfaceClass = FilmService.class, check = false)
    FilmService filmService;
    @Reference(interfaceClass = CinemaService.class, check = false)
    CinemaService cinemaService;

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
    public List<OrderInfo> getOrderByUserId(int userId, OrderPage page) {
        Integer nowPage = page.getNowPage();
        Integer pageSize = page.getPageSize();
        Page<MoocOrderT> orderPage = new Page<>(nowPage, pageSize);
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        wrapper.eq("order_user", userId);
        List<MoocOrderT> orders = orderTMapper.selectPage(orderPage, wrapper);
        List<OrderInfo> orderList = new ArrayList<>();
        for (MoocOrderT order : orders) {
            OrderInfo orderInfo = new OrderInfo();
            String filmName = filmService.getNameById(order.getFilmId());
            String fieldTime = cinemaService.getFieldTimeById(order.getFieldId());
            String cinemaName = cinemaService.getNameById(order.getCinemaId());
            orderInfo.setOrderId(order.getUuid());
            orderInfo.setFilmName(filmName);
            orderInfo.setFieldTime(fieldTime);
            orderInfo.setCinemaName(cinemaName);
            orderInfo.setSeatsName(order.getSeatsName());
            orderInfo.setOrderPrice(order.getOrderPrice().toString());
            Integer status = order.getOrderStatus();
            switch(status) {
                case 0: orderInfo.setOrderStatus("待支付");
                    break;
                case 1: orderInfo.setOrderStatus("已支付");
                    break;
                case 2: orderInfo.setOrderStatus("已关闭");
                    break;
            }
            orderList.add(orderInfo);
        }
        return orderList;
    }
}
