package com.stylefeng.guns.rest.config.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayProperties {

    private static Log log = LogFactory.getLog(AlipayProperties.class);

    private static String openApiDomain;
    private static String mcloudApiDomain;
    private static String pid;           //商户UID
    private static String appid;         // appid
    private static String privateKey;      // RSA私钥，用于对商户请求报文加签
    private static String publicKey;       // RSA公钥，仅用于验证开发者网关
    private static String alipayPublicKey; // 支付宝RSA公钥，用于验签支付宝应答
    private static String signType;     // 签名类型
    private static int maxQueryRetry;   // 最大查询次数
    private static long queryDuration;  // 查询间隔（毫秒）
    private static int maxCancelRetry;  // 最大撤销次数
    private static long cancelDuration; // 撤销间隔（毫秒）
    private static long heartbeatDelay ; // 交易保障线程第一次调度延迟（秒）
    private static long heartbeatDuration ; // 交易保障线程调度间隔（秒）


    public String getOpenApiDomain() {
        return openApiDomain;
    }

    public void setOpenApiDomain(String openApiDomain) {
        this.openApiDomain = openApiDomain;
    }

    public String getMcloudApiDomain() {
        return mcloudApiDomain;
    }

    public void setMcloudApiDomain(String mcloudApiDomain) {
        this.mcloudApiDomain = mcloudApiDomain;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public static Log getLog() {
        return log;
    }

    public static void setLog(Log log) {
        AlipayProperties.log = log;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public int getMaxQueryRetry() {
        return maxQueryRetry;
    }

    public void setMaxQueryRetry(int maxQueryRetry) {
        this.maxQueryRetry = maxQueryRetry;
    }

    public long getQueryDuration() {
        return queryDuration;
    }

    public void setQueryDuration(long queryDuration) {
        this.queryDuration = queryDuration;
    }

    public int getMaxCancelRetry() {
        return maxCancelRetry;
    }

    public void setMaxCancelRetry(int maxCancelRetry) {
        this.maxCancelRetry = maxCancelRetry;
    }

    public long getCancelDuration() {
        return cancelDuration;
    }

    public void setCancelDuration(long cancelDuration) {
        this.cancelDuration = cancelDuration;
    }

    public long getHeartbeatDelay() {
        return heartbeatDelay;
    }

    public void setHeartbeatDelay(long heartbeatDelay) {
        this.heartbeatDelay = heartbeatDelay;
    }

    public long getHeartbeatDuration() {
        return heartbeatDuration;
    }

    public void setHeartbeatDuration(long heartbeatDuration) {
        this.heartbeatDuration = heartbeatDuration;
    }
}
