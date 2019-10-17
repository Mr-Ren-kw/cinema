package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Service;
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
import com.stylefeng.guns.rest.alipay.service.AlipayTradeService;
import com.stylefeng.guns.rest.alipay.service.impl.AlipayTradeServiceImpl;
import com.stylefeng.guns.rest.alipay.utils.Utils;
import com.stylefeng.guns.rest.alipay.utils.ZxingUtils;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.config.properties.AlipayProperties;
import com.stylefeng.guns.rest.config.properties.OssProperties;
import com.stylefeng.guns.rest.order.OrderService;
import com.stylefeng.guns.rest.order.vo.OrderData;
import com.stylefeng.guns.rest.order.vo.OrderRespVo;
import com.stylefeng.guns.rest.order.vo.OrderResultResponseVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {
    private static Log log = LogFactory.getLog(OrderServiceImpl.class);
    @Autowired
    MoocOrderTMapper orderTMapper;

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
