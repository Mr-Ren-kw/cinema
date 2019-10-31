package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.fastjson.JSON;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import com.stylefeng.guns.rest.config.properties.RocketmqProperties;
import com.stylefeng.guns.rest.promo.vo.StockLogVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
public class RocketMQConsumer {

    @Autowired
    MtimePromoStockMapper promoStockMapper;

    private DefaultMQPushConsumer consumer;

    @PostConstruct
    public void init() {
        consumer = new DefaultMQPushConsumer(RocketmqProperties.consumerGroup);
        consumer.setNamesrvAddr(RocketmqProperties.host);
        try {
            consumer.subscribe(RocketmqProperties.topic, RocketmqProperties.tag);
        } catch (MQClientException e) {
            e.printStackTrace();
            log.info("MQconsumer订阅失败！");
        }
        log.info("MQconsumer订阅成功！...");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt messageExt = list.get(0);
                byte[] body = messageExt.getBody();
                String s = new String(body);
                StockLogVO stockLogVO = JSON.parseObject(s, StockLogVO.class);
                Integer promoId = stockLogVO.getPromoId();
                Integer amount = stockLogVO.getAmount();

                Integer affectedRows = promoStockMapper.decreaseStock(promoId, amount);
                log.info(affectedRows + "");
                if(affectedRows < 1) {
                    log.info("消费失败！扣减库存失败，promoId:{},amount:{}",promoId,amount);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 用不到了
//     * @return
//     */
//    public boolean AsyncDecreaseStock() {
//        consumer.registerMessageListener(new MessageListenerConcurrently() {
//            @Override
//            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
//                MessageExt messageExt = list.get(0);
//                MtimePromoStock stock = JSON.parseObject(new String(messageExt.getBody()), MtimePromoStock.class);
//                Integer res = promoStockMapper.updateStock(stock);
//                if (res < 1) {
//                    log.info("扣减库存失败。。。");
//                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
//                }
//                log.info("扣减库存成功：consumer " + stock);
//                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//            }
//        });
//        try {
//            consumer.start();
//        } catch (MQClientException e) {
//            e.printStackTrace();
//        }
//        log.info("MQconsumer启动成功!");
//        return true;
//    }
}
