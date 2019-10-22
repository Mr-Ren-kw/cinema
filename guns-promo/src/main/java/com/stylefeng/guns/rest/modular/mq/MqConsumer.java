package com.stylefeng.guns.rest.modular.mq;

import com.alibaba.fastjson.JSON;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class MqConsumer {

    @Value("${mq.name-server.address}")
    private String address;

    @Value("${mq.topic}")
    private String topic;

    @Value("${mq.consumer-group}")
    private String consumerGroup;

    @Autowired
    MtimePromoStockMapper stockMapper;

    private DefaultMQPushConsumer consumer;

    @PostConstruct
    public void init() {
        consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(address);
        try {
            consumer.subscribe(topic, "*");
        } catch (MQClientException e) {
            e.printStackTrace();
            log.info("订阅失败！");
        }

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            /**
             * 执行订阅方的事务，扣减数据库中的库存
             * @param list
             * @param consumeConcurrentlyContext
             * @return
             */
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt messageExt = list.get(0);
                String bodyStr = new String(messageExt.getBody());
                HashMap<String, Object> messageMap = JSON.parseObject(bodyStr, HashMap.class);
                Integer promoId = (Integer) messageMap.get("promoId");
                Integer amount = (Integer) messageMap.get("amount");
                int affectedRows = stockMapper.updateStockByPromoId(promoId, amount);
                if (affectedRows != 1) {
                    log.info("消费失败！扣减库存失败！promoId:{},amount:{}",promoId,amount);
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

}
