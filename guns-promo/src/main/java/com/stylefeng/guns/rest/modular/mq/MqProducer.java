package com.stylefeng.guns.rest.modular.mq;

import com.alibaba.fastjson.JSON;
import com.stylefeng.guns.core.constant.StockLogStatus;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeStockLogMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeStockLog;
import com.stylefeng.guns.rest.promo.PromoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;

@Component
@Slf4j
public class MqProducer {

    @Value("${mq.name-server.address}")
    private String address;

    @Value("${mq.topic}")
    private String topic;

    @Value("${mq.producer-group}")
    private String producerGroup;

    @Autowired
    PromoService promoService;

    @Autowired
    MtimeStockLogMapper stockLogMapper;

    private TransactionMQProducer producer;

    @PostConstruct
    public void init() {
        producer = new TransactionMQProducer(producerGroup);
        producer.setNamesrvAddr(address);
        // 启动
        try {
            producer.start();
            log.info("MqProducer启动成功。。。");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        // 设置事务监听器
        producer.setTransactionListener(new TransactionListener() {
            /**
             * 执行本地事务，包括创建订单、修改redis中的库存、新插一条库存流水表
             * @param message 订阅方执行事务所需要的参数
             * @param args 执行本地事务所需要的参数
             * @return 执行本地事务的状态码，由此决定是commit还是rollback
             */
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object args) {
                HashMap<String,Object> argsMap = (HashMap<String, Object>) args;
                Integer promoId = (Integer) argsMap.get("promoId");
                Integer amount = (Integer) argsMap.get("amount");
                Integer userId = (Integer) argsMap.get("userId");
                String stockLogId = (String) argsMap.get("stockLogId");
                // 创建订单
                boolean result = promoService.createPromoOrder(promoId, amount, userId, stockLogId);
                if (result) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                }
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }

            /**
             * 当订阅方未收到提供方的提交或回滚消息时，检查本地事务的执行结果（通过检查库存流水表中的状态）
             * @param messageExt
             * @return
             */
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                String jsonStr = new String(messageExt.getBody());
                HashMap<String, Object> messageMap = JSON.parseObject(jsonStr, HashMap.class);
                String stockLogId = (String) messageMap.get("stockLogId");
                // 查询流水表获取状态
                MtimeStockLog stockLog = stockLogMapper.selectById(stockLogId);
                Integer status = stockLog.getStatus();
                if (StockLogStatus.SUCCESS.getIndex() == status) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                } else if (StockLogStatus.FAIL.getIndex() == status) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                } else {
                    return LocalTransactionState.UNKNOW;
                }
            }
        });
    }

    /**
     * 发送事务型消息
     * @param promoId 秒杀活动编号
     * @param userId 用户id
     * @param amount 购买数量
     * @param stockLogId 生成的秒杀流水表的uuid
     * @return 创建成功返回true，否则返回false
     */
    public boolean transactionCreateOrder(Integer promoId,Integer userId,Integer amount,String stockLogId) {
        // 为Message对象封装参数
        HashMap<String,Object> messageMap = new HashMap<>();
        messageMap.put("promoId", promoId);
        messageMap.put("amount", amount);
        messageMap.put("stockLogId", stockLogId);
        // 为args封装参数
        HashMap<String,Object> argsMap = new HashMap<>();
        argsMap.put("promoId", promoId);
        argsMap.put("userId", userId);
        argsMap.put("amount", amount);
        argsMap.put("stockLogId", stockLogId);
        // 发送事务消息
        TransactionSendResult sendResult = null;
        Message message = new Message(topic, JSON.toJSONString(messageMap).getBytes(Charset.forName("utf-8")));
        try {
            sendResult = producer.sendMessageInTransaction(message, argsMap);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        }
        if (sendResult == null) {
            return false;
        }
        return SendStatus.SEND_OK == sendResult.getSendStatus();
    }
}
