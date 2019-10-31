package com.stylefeng.guns.rest.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rocketmq")
public class RocketmqProperties {
    public static String host;
    public static String topic;
    public static String tag;
    public static String consumerGroup;
    public static String producerGroup;
    public static String transactionGroup;

    public String getTransactionGroup() {
        return transactionGroup;
    }

    public void setTransactionGroup(String transactionGroup) {
        RocketmqProperties.transactionGroup = transactionGroup;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        RocketmqProperties.consumerGroup = consumerGroup;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        RocketmqProperties.producerGroup = producerGroup;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        RocketmqProperties.host = host;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        RocketmqProperties.topic = topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        RocketmqProperties.tag = tag;
    }
}
