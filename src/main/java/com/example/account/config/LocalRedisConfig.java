package com.example.account.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Configuration
public class LocalRedisConfig {

    @Value("${spring.redis.port}")
    private int redisPort;
    private RedisServer redisServer;
    @PostConstruct
    public void startRedis() {
        redisServer = RedisServer.builder()
                .port(redisPort)
                .setting("maxmemory 1024M").build();
        //redisServer = new RedisServer(redisPort);
        try{
            redisServer.start();
        }
        catch(Exception e){

        }

    }
    @PreDestroy
    public void endRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
