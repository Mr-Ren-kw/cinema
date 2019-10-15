package com.stylefeng.guns.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class JedisConfig {

    /**
     * 在容器中注册一个jedis对象
     * @return
     */
    @Bean
    public Jedis jedis() {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        return jedis;
    }
}
