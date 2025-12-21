package com.restaurant.creditmanagement.config;

import com.restaurant.creditmanagement.service.RedisMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.URI;

@Configuration
public class RedisConfig {

    @Autowired
    private RedisMessageListener redisMessageListener;

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Value("${spring.redis.url:}")
    private String redisUrl;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        if (!redisUrl.isEmpty()) {
            // Parse Redis URL for cloud Redis (redis://username:password@host:port)
            try {
                URI redisUri = URI.create(redisUrl);
                RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
                config.setHostName(redisUri.getHost());
                config.setPort(redisUri.getPort());

                // Extract password from URL if present
                String userInfo = redisUri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String password = userInfo.split(":")[1];
                    config.setPassword(password);
                }

                return new LettuceConnectionFactory(config);
            } catch (Exception e) {
                throw new RuntimeException("Invalid Redis URL format: " + redisUrl, e);
            }
        } else {
            // Use individual host/port configuration for local Redis
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(redisHost);
            config.setPort(redisPort);
            if (!redisPassword.isEmpty()) {
                config.setPassword(redisPassword);
            }
            return new LettuceConnectionFactory(config);
        }
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("restaurant-orders");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(redisMessageListener, topic());
        return container;
    }
}