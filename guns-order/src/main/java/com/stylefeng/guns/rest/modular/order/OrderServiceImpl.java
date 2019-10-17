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
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.aliyun.oss.OSS;
import com.stylefeng.guns.rest.alipay.model.ExtendParams;
import com.stylefeng.guns.rest.alipay.model.GoodsDetail;
import com.stylefeng.guns.rest.alipay.model.builder.AlipayTradePrecreateRequestBuilder;
import com.stylefeng.guns.rest.alipay.model.builder.AlipayTradeQueryRequestBuilder;
import com.stylefeng.guns.rest.alipay.model.result.AlipayF2FPrecreateResult;
import com.stylefeng.guns.rest.alipay.model.result.AlipayF2FQueryResult;
import com.stylefeng.guns.rest.alipay.service.impl.AlipayTradeServiceImpl;
import com.stylefeng.guns.rest.alipay.utils.Utils;
import com.stylefeng.guns.rest.alipay.utils.ZxingUtils;
import com.stylefeng.guns.rest.config.properties.AlipayProperties;
import com.stylefeng.guns.rest.config.properties.OssProperties;
import com.stylefeng.guns.rest.order.OrderService;
import com.stylefeng.guns.rest.order.vo.OrderData;
import com.stylefeng.guns.rest.order.vo.OrderRespVo;
import com.stylefeng.guns.rest.order.vo.OrderResultResponseVO;
import com.stylefeng.guns.rest.order.vo.OrderInfo;
import com.stylefeng.guns.rest.order.vo.OrderPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

import java.io.File;

@Component
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {
    private static Log log = LogFactory.getLog(OrderServiceImpl.class);
    @Autowired
    MoocOrderTMapper orderTMapper;

    @Autowired
    Jedis jedis;

    @Reference(interfaceClass = CinemaService.class,check = false)
    CinemaService cinemaService;

    @Reference(interfaceClass = FilmService.class,check = false)
    FilmService filmService;

    @Autowired
    OssProperties ossProperties;

    @Autowired
    AlipayProperties alipayProperties;

    @Autowired
    AlipayTradeServiceImpl alipayTradeService;

    @Autowired
    OSS oss;

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
    public OrderResultResponseVO getPayResult(String orderId) {
        OrderResultResponseVO orderResultResponseVO = new OrderResultResponseVO();
        orderResultResponseVO.setOrderId(orderId);
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder().setOutTradeNo(orderId);
        AlipayF2FQueryResult res = alipayTradeService.queryTradeResult(builder);
        switch (res.getTradeStatus()) {
            case SUCCESS:
                log.info("查询返回该订单支付成功: )");
                AlipayTradeQueryResponse response = res.getResponse();
                log.info(response.getTradeStatus());
                if (Utils.isListNotEmpty(response.getFundBillList())) {
                    for (TradeFundBill bill : response.getFundBillList()) {
                        log.info(bill.getFundChannel() + ":" + bill.getAmount());
                    }
                }
                // 将数据库订单的status改为1
                orderTMapper.updateOrderStatusByUuid(orderId, 1);
                // status = 1 则为支付成功
                orderResultResponseVO.setOrderStatus(1);
                orderResultResponseVO.setOrderMsg("支付成功");
                return orderResultResponseVO;

            case FAILED:
                log.error("查询返回该订单支付失败或被关闭!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，订单支付状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        // 失败
        orderResultResponseVO.setOrderStatus(0);
        orderResultResponseVO.setOrderMsg("订单支付失败，请稍后重试");
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

    public OrderRespVo<OrderResultResponseVO> getPayInfo(String orderId) {
        MoocOrderT moocOrderT = orderTMapper.selectByUuid(orderId);
        if(moocOrderT == null) {
            return null;
        }
        OrderRespVo<OrderResultResponseVO> res = new OrderRespVo<>();
        res.setImgPre(ossProperties.getImgPre());
        res.setStatus(0);
        OrderResultResponseVO orderResultResponseVO = new OrderResultResponseVO();
        orderResultResponseVO.setOrderId(orderId);
        //支付宝生成二维码
        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        double film_price = moocOrderT.getFilmPrice();
        double order_price = moocOrderT.getOrderPrice();
        int number = (int) (order_price / film_price);
        String body = String.format("购买商品%d件共%f元", number, order_price);
        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "110"; // 随便填的
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(alipayProperties.getPid());
        String sellerId = alipayProperties.getPid();
        String timeoutExpress = alipayProperties.getTimeout();
        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        GoodsDetail goods = GoodsDetail.newInstance(moocOrderT.getFilmId() + "", "电影票", (long) film_price, number);
        // 创建好一个商品后添加至商品明细列表
        goodsDetailList.add(goods);
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(alipayProperties.getSubject()).setTotalAmount(order_price + "").setOutTradeNo(moocOrderT.getUuid())
                .setSellerId(sellerId).setBody(body)
                .setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                //                .setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);
//        AlipayTradeServiceImpl alipayTradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
        AlipayF2FPrecreateResult result = alipayTradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();

                // 需要修改为运行机器上的路径
                String fileName = String.format("qr-%s.png", response.getOutTradeNo());
                String filePath = ossProperties.getFilePath() + fileName;
                log.info("filePath:" + filePath);
                File qrCodeImge = ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                oss.putObject(ossProperties.getBucketName(), fileName, qrCodeImge);
                orderResultResponseVO.setQRCodeAddress(fileName);
                res.setData(orderResultResponseVO);
                return res;
            case FAILED:
                log.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        return null;
    }
}
