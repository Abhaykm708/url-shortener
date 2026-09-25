package com.abhay.urlshortener.common.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisConnectionTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void redisTest_Work() {
        redisTemplate.opsForValue()
                .set("redis:test", "Connection Successfull");

        String value=redisTemplate.opsForValue()
                .get("redis:set");

        Assertions.assertEquals(
                "Connection Successfull",
                value
        );
    }
}
