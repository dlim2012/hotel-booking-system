package com.dlim2012.searchconsumer.config.kafka.consumer.rooms;

import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchDeleteRequest;
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
        "com.dlim2012.searchconsumer"
})
@RequiredArgsConstructor
public class RoomsSearchDeleteKafkaConsumerConfig {
    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Bean
    public NewTopic roomSearchDeleteTopic(){
        return TopicBuilder.name("rooms-search-delete").build();
    }

    @Bean
    public ConsumerFactory<String, RoomsSearchDeleteRequest> roomSearchDeleteConsumerFactory() {
        JsonDeserializer<RoomsSearchDeleteRequest> jsonDeserializer = new JsonDeserializer<>(RoomsSearchDeleteRequest.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RoomsSearchDeleteRequest> roomsSearchDeleteKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RoomsSearchDeleteRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(roomSearchDeleteConsumerFactory());
        return factory;
    }
}
