package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfiguration {
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public JedisConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Url> redisTemplate() {
        RedisTemplate<String, Url> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();

        return template;
    }

}