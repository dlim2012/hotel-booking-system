package com.dlim2012.booking.config;

import com.dlim2012.booking.controller.RedisExpirationListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisListenerConfig {
    // notify-keyspace-events Ex (redis cli: config set notify-keyspace-events Ex)


    private final String PATTERN = "__keyevent@*__:expired";

//    private final String PATTERN = "task_with_max_wait_time__1";

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory,
            RedisExpirationListener redisExpirationListener) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        redisMessageListenerContainer.addMessageListener(redisExpirationListener, new PatternTopic(PATTERN));
        redisMessageListenerContainer.setErrorHandler(e -> log.error("There was an error in redis key expiration listener container", e));
        return redisMessageListenerContainer;
    }

}

