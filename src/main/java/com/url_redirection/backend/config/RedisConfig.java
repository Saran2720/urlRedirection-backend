package com.url_redirection.backend.config;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
public class RedisConfig {
    @Bean
    public LettuceConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory();
    }

    @Bean
    public StringRedisTemplate redisTemplate(){
        return new StringRedisTemplate(redisConnectionFactory());
    }
}
