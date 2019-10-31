package com.stylefeng.guns.rest.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {
    private int expiration = 3600; // 库存过期时间一个小时
    private String  prefix = "stock_"; // key = 'stock_' + promoId
    private String log = "log";
    private int tokenFactor = 5;

    public int getTokenFactor() {
        return tokenFactor;
    }

    public void setTokenFactor(int tokenFactor) {
        this.tokenFactor = tokenFactor;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public int getExpiration() {
        return expiration;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
