package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.fastjson.JSON;
import com.stylefeng.guns.rest.common.enums.StockLogStatus;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeStockLogMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeStockLog;
import com.stylefeng.guns.rest.config.properties.RocketmqProperties;
import com.stylefeng.guns.rest.promo.PromotionService;
import com.stylefeng.guns.rest.promo.vo.StockLogVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Slf4j
@Component
public class RocketMQProducer {
    private TransactionMQProducer producer;

    @Autowired
    PromotionService promotionService;

    @Autowired
    MtimeStockLogMapper stockLogMapper;

    @PostConstruct
    public void init() {
        producer = new TransactionMQProducer(RocketmqProperties.transactionGroup);
        producer.setNamesrvAddr(RocketmqProperties.host);
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        log.info("MQproducer启动成功!");
        producer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object arg) {
                if (arg == null) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                StockLogVO stockLogVO = (StockLogVO) arg;
                Integer amount = stockLogVO.getAmount();
                Integer promoId = stockLogVO.getPromoId();
                String stockLogId = stockLogVO.getStockLogId();
                Integer userId = stockLogVO.getUserId();
                boolean res = false;
                try {
                    res = promotionService.createOrder(userId, promoId, amount, stockLogId);
                } catch (Exception e) {
                    e.printStackTrace();
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                if (!res) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            /**
             * 根据库存流水状态的情况
             * 1，库存流水状态是成功
             * 返回COMMIT_MESSAGE
             * 2，库存流水状态是失败
             * 返回ROLLBACK_MESSAGE
             * 3，库存流水状态是初始化的
             * UNKNOW
             * @param messageExt
             * @return LocalTransactionState
             */
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                String body = new String(messageExt.getBody());
                StockLogVO stockLogVO = JSON.parseObject(body, StockLogVO.class);
                String stockLogId = stockLogVO.getStockLogId();
                MtimeStockLog stockLog = stockLogMapper.selectById(stockLogId);
                Integer status = stockLog.getStatus();
                if(StockLogStatus.SUCCESS.getIndex() == status) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                } else if(StockLogStatus.FAIL.getIndex() == status) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                } else {
                    return LocalTransactionState.UNKNOW;
                }
            }
        });
    }

//    public SendResult sendMsg(T msg) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
//        String s = JSON.toJSONString(msg);
//        Message message = new Message(RocketmqProperties.topic, RocketmqProperties.tag , s.getBytes(StandardCharsets.UTF_8));
//        SendResult send = producer.send(message);
//        return send;
//    }

    public Boolean transactionCreateOrder(int userId, Integer promoId, Integer amount, String stockLogId) {
        StockLogVO mess = new StockLogVO();
        mess.setStockLogId(stockLogId);
        mess.setPromoId(promoId);
        mess.setAmount(amount);
        mess.setUserId(userId);
        String jsonString = JSON.toJSONString(mess);

        StockLogVO arg = new StockLogVO();
        arg.setStockLogId(stockLogId);
        arg.setPromoId(promoId);
        arg.setAmount(amount);
        arg.setUserId(userId);

        TransactionSendResult transactionSendResult = null;
        Message message = new Message(RocketmqProperties.topic, RocketmqProperties.tag, jsonString.getBytes(StandardCharsets.UTF_8));
        try {
            transactionSendResult = producer.sendMessageInTransaction(message,arg);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        }
        if (transactionSendResult == null) {
            return false;
        }
        LocalTransactionState localTransactionState = transactionSendResult.getLocalTransactionState();
        if (LocalTransactionState.COMMIT_MESSAGE.equals(localTransactionState)) {
            return true;
        }
        return false;
    }
}
