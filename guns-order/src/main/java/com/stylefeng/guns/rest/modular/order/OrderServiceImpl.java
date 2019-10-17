package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.cinema.vo.FieldMsgForOrder;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.film.FilmService;
import com.stylefeng.guns.rest.order.OrderService;
import com.stylefeng.guns.rest.order.vo.OrderData;
import com.stylefeng.guns.rest.order.vo.OrderResultResponseVO;
import com.stylefeng.guns.rest.order.vo.OrderInfo;
import com.stylefeng.guns.rest.order.vo.OrderPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {
    @Autowired
    MoocOrderTMapper orderTMapper;

    @Autowired
    Jedis jedis;

    @Reference(interfaceClass = CinemaService.class,check = false)
    CinemaService cinemaService;

    @Reference(interfaceClass = FilmService.class,check = false)
    FilmService filmService;

    @Override
    public boolean isTrueSeats(int fieldId, String seats) {
        // 获取json文件地址
        String hallSeatIds = cinemaService.getHallSeatIds(fieldId);
        // 比较
        String[] hallSeatList = hallSeatIds.split(",");
        String s1 = UUID.randomUUID().toString().replaceAll("-","");
        jedis.sadd(s1, hallSeatList);
        String[] seatList = seats.split(",");
        String s2 = UUID.randomUUID().toString().replaceAll("-","");
        jedis.sadd(s2, seatList);
        Set<String> sdiff = jedis.sdiff(s2, s1);
        jedis.del(s1,s2);
        return sdiff.size() == 0;
    }

    @Override
    public boolean isNotSoldSeats(int fieldId, String seats) {
        // 先去order表中查询该场次已售的座位号
        List<String> soldSeatList = orderTMapper.getSoldSeatList(fieldId);
        // 将所有座位放到一个list中
        List<String> seatList = new LinkedList<>();
        String[] split = null;
        for (String sordSeat : soldSeatList) {
            split = sordSeat.split(",");
            seatList.addAll(Arrays.asList(split));
        }
        // 转换成String数组放到redis中
        String[] seatArray = new String[seatList.size()];
        seatList.toArray(seatArray);
        String s1 = UUID.randomUUID().toString().replaceAll("-","");
        jedis.sadd(s1,seatArray);
        // 将用户提交的座位号转成数组放到redis中
        String[] userSeatArray = seats.split(",");
        String s2 = UUID.randomUUID().toString().replaceAll("-","");
        jedis.sadd(s2,userSeatArray);
        // 比较两边是否有交集
        Set<String> sinter = jedis.sinter(s1, s2);
        jedis.del(s1, s2);
        return sinter.size() == 0;
    }

    @Override
    public OrderData saveOrderInfo(int fieldId, String soldSeats, String seatsName, int userId) {
        // 先查询field表获取该场次的信息
        FieldMsgForOrder fieldMsg = cinemaService.getFieldMsgById(fieldId);
        // 查询用户购了几张票，生成订单总金额
        String[] split = soldSeats.split(",");
        double orderPrice = fieldMsg.getPrice() * split.length;
        // 生成订单
        MoocOrderT moocOrderT = new MoocOrderT();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        moocOrderT.setUuid(uuid.substring(uuid.length() - 32));
        moocOrderT.setCinemaId(fieldMsg.getCinemaId());
        moocOrderT.setFieldId(fieldId);
        moocOrderT.setFilmId(fieldMsg.getFilmId());
        moocOrderT.setSeatsIds(soldSeats);
        moocOrderT.setSeatsName(seatsName);
        moocOrderT.setFilmPrice(fieldMsg.getPrice());
        moocOrderT.setOrderPrice(orderPrice);
        moocOrderT.setOrderUser(userId);
        orderTMapper.insertNewOrder(moocOrderT);
        // 取出订单
        moocOrderT = orderTMapper.selectMoocOrderTById(moocOrderT.getUuid());
        OrderData orderData = new OrderData();
        orderData.setOrderId(moocOrderT.getUuid());
        // 查询film表
        orderData.setFilmName(filmService.getFilmNameById(moocOrderT.getFilmId()));
        // 查询field表
        orderData.setFieldTime(cinemaService.getFieldTimeById(moocOrderT.getFieldId()));
        // 查询cinema表
        orderData.setCinemaName(cinemaService.getCinemaNameById(moocOrderT.getCinemaId()));
        orderData.setSeatsName(seatsName);
        orderData.setOrderPrice(String.valueOf(orderPrice));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String orderTimestamp = format.format(moocOrderT.getOrderTime());
        orderData.setOrderTimestamp(orderTimestamp);
        return orderData;
    }

    @Override
    public String getSoldSeats(int fieldId) {
        List<String> soldSeats = orderTMapper.getSoldSeatList(fieldId);
        StringBuffer stringBuffer = new StringBuffer();
        for (String soldSeat : soldSeats) {
            stringBuffer.append(soldSeat).append(",");
        }
        String string = stringBuffer.toString();
        return string.substring(0, string.length() - 1);
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
