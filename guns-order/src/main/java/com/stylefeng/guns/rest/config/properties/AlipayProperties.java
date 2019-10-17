package com.stylefeng.guns.rest.config.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayProperties {

    Log log = LogFactory.getLog(AlipayProperties.class);
    public static String subject; //(必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
    public static String timeout; //支付超时，定义为120分钟
    public static String openApiDomain;
    public static String mcloudApiDomain;
    public static String pid;           //商户UID
    public static String appid;         // appid
    public static String privateKey;      // RSA私钥，用于对商户请求报文加签
    public static String publicKey;       // RSA公钥，仅用于验证开发者网关
    public static String alipayPublicKey; // 支付宝RSA公钥，用于验签支付宝应答
    public static String signType;     // 签名类型
    public static int maxQueryRetry;   // 最大查询次数
    public static long queryDuration;  // 查询间隔（毫秒）
    public static int maxCancelRetry;  // 最大撤销次数
    public static long cancelDuration; // 撤销间隔（毫秒）
    public static long heartbeatDelay ; // 交易保障线程第一次调度延迟（秒）
    public static long heartbeatDuration ; // 交易保障线程调度间隔（秒）


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        AlipayProperties.subject = subject;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        AlipayProperties.timeout = timeout;
    }

    public String getOpenApiDomain() {
        return openApiDomain;
    }

    public void setOpenApiDomain(String openApiDomain) {
        AlipayProperties.openApiDomain = openApiDomain;
    }

    public String getMcloudApiDomain() {
        return mcloudApiDomain;
    }

    public void setMcloudApiDomain(String mcloudApiDomain) {
        AlipayProperties.mcloudApiDomain = mcloudApiDomain;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        AlipayProperties.pid = pid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        AlipayProperties.appid = appid;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        AlipayProperties.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        AlipayProperties.publicKey = publicKey;
    }

    public String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public void setAlipayPublicKey(String alipayPublicKey) {
        AlipayProperties.alipayPublicKey = alipayPublicKey;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        AlipayProperties.signType = signType;
    }

    public int getMaxQueryRetry() {
        return maxQueryRetry;
    }

    public void setMaxQueryRetry(int maxQueryRetry) {
        AlipayProperties.maxQueryRetry = maxQueryRetry;
    }

    public long getQueryDuration() {
        return queryDuration;
    }

    public void setQueryDuration(long queryDuration) {
        AlipayProperties.queryDuration = queryDuration;
    }

    public int getMaxCancelRetry() {
        return maxCancelRetry;
    }

    public void setMaxCancelRetry(int maxCancelRetry) {
        AlipayProperties.maxCancelRetry = maxCancelRetry;
    }

    public long getCancelDuration() {
        return cancelDuration;
    }

    public void setCancelDuration(long cancelDuration) {
        AlipayProperties.cancelDuration = cancelDuration;
    }

    public long getHeartbeatDelay() {
        return heartbeatDelay;
    }

    public void setHeartbeatDelay(long heartbeatDelay) {
        AlipayProperties.heartbeatDelay = heartbeatDelay;
    }

    public long getHeartbeatDuration() {
        return heartbeatDuration;
    }

    public void setHeartbeatDuration(long heartbeatDuration) {
        AlipayProperties.heartbeatDuration = heartbeatDuration;
    }
}
