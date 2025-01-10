package com.u012e.session_auth_db.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.*;

@Configuration
public class RedisConfiguration {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private Integer port;

    @Bean("redis")
    JedisConnectionFactory connectionFactory() {
        var connectionFactory = new JedisConnectionFactory();
        connectionFactory.setHostName(host);
        connectionFactory.setPort(port);
        return connectionFactory;
    }

    @Bean
    @Primary
    public <T> RedisTemplate<String, T> defaultRedisTemplate(
            @Qualifier("redis")
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @Primary
    public <T> ValueOperations<String, T> defaultValueOperations(RedisTemplate<String, T> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    UnifiedJedis jedis(JedisConnectionFactory jedisConnectionFactory) {
        return new UnifiedJedis(new HostAndPort(host, port));
    }
}
