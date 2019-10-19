package com.stylefeng.guns.rest;

import com.alibaba.fastjson.JSON;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import com.stylefeng.guns.rest.common.persistence.model.PromoNewStock;
import com.stylefeng.guns.rest.modular.promo.PromoServiceImpl;
import com.stylefeng.guns.rest.promo.PromoService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MQConsumerForStock {
    @Autowired
    static MtimePromoStockMapper stockMapper;

    public static void main(String[] args) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer-stock");
        consumer.setNamesrvAddr("127.0.0.1:9876");
        try {
            consumer.subscribe("stock","*");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt messageExt = list.get(0);
                String s = new String(messageExt.getBody());
                PromoNewStock promoNewStock = JSON.parseObject(s, PromoNewStock.class);
                int result = stockMapper.updateStockByPromoId(promoNewStock.getPromoId(), promoNewStock.getNewStock());
                return result == 1 ? ConsumeConcurrentlyStatus.CONSUME_SUCCESS : ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }
}
