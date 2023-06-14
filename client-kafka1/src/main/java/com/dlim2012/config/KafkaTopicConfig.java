package com.dlim2012.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ComponentScan(basePackages = {"com.dlim2012.hotel", "com.dlim2012.searchconsumer"})
public class KafkaTopicConfig {

    @Bean
    public NewTopic roomTopic(){
        return TopicBuilder.name("room").build();
    }

    @Bean
    public NewTopic hotelTopic(){
        return TopicBuilder.name("hotel").build();
    }
}
