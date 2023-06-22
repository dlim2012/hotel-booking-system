package com.dlim2012.clients.kafka.config;

import com.dlim2012.clients.dto.hotel.RoomItem;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;


@Configuration
@EnableKafka
@ComponentScan(basePackages = {
        "com.dlim2012.hotel",
        "com.dlim2012.searchconsumer",
        "com.dlim2012.booking"})
@RequiredArgsConstructor
public class KafkaRoomConsumerConfig {
    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Bean
    public NewTopic roomTopic(){
        return TopicBuilder.name("room").build();
    }

    @Bean
    public ConsumerFactory<String, RoomItem> roomItemConsumerFactory() {
        JsonDeserializer<RoomItem> jsonDeserializer = new JsonDeserializer<>(RoomItem.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RoomItem> roomItemKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RoomItem> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(roomItemConsumerFactory());
        return factory;
    }
}
