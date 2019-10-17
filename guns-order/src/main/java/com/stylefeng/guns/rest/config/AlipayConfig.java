package com.stylefeng.guns.rest.config;

import com.stylefeng.guns.rest.alipay.service.AlipayTradeService;
import com.stylefeng.guns.rest.alipay.service.impl.AlipayTradeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayConfig {

    @Bean
    public AlipayTradeServiceImpl alipayTradeServiceImpl() {
        return new AlipayTradeServiceImpl.ClientBuilder().build();
    }


}
