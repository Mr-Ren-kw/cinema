package com.stylefeng.guns.rest.common.util;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.Charset;

public class MQProducerForStock {
    private static DefaultMQProducer producer = new DefaultMQProducer("producer-stock");
    public static void sendMessage(Object o) throws MQClientException {
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.start();
        Message message = new Message("stock", "tag111", JSON.toJSONString(o).getBytes(Charset.forName("utf-8")));
        SendResult sendResult = null;
        try {
            sendResult = producer.send(message);
        } catch (RemotingException | InterruptedException | MQBrokerException e) {
            e.printStackTrace();
        }
    }
}
