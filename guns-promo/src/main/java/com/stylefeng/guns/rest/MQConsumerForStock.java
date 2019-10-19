package com.stylefeng.guns.rest;

import com.alibaba.fastjson.JSON;
import com.stylefeng.guns.rest.common.persistence.model.PromoNewStock;
import com.stylefeng.guns.rest.modular.promo.PromoServiceImpl;
import com.stylefeng.guns.rest.promo.PromoService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class MQConsumerForStock {
    public static void main(String[] args) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer-stock");
        consumer.setNamesrvAddr("127.0.0.1:9876");
        try {
            consumer.subscribe("stock","*");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        PromoService promoService = new PromoServiceImpl();
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt messageExt = list.get(0);
                String s = new String(messageExt.getBody());
                PromoNewStock promoNewStock = JSON.parseObject(s, PromoNewStock.class);
                int result = promoService.updatePromoStock(promoNewStock.getPromoId(), promoNewStock.getNewStock());
                return result == 1 ? ConsumeConcurrentlyStatus.CONSUME_SUCCESS : ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
    }
}
