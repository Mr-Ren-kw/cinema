package com.stylefeng.guns.rest.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class CacheService {

    private Cache<String,Object> cache;

    @PostConstruct
    public void init() {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .initialCapacity(20)    // 设置初始容量大小
                .maximumSize(100)       // 设置最大容量，达到最大容量之后会按照LRU缓存清除策略进行淘汰
                .build();
    }


    public void set(String key, Object value) {
        cache.put(key,value);
    }

    public Object get(String key) {
        return cache.getIfPresent(key); // 如果存在则拿出，否则返回null
    }
}
