package com.dlim2012.searchconsumer.config.kafka.consumer.rooms;

import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchDetails;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchVersion;
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
public class RoomsSearchVersionKafkaConsumerConfig {
    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Bean
    public NewTopic roomSearchVersionTopic(){
        return TopicBuilder.name("rooms-search-version").build();
    }

    @Bean
    public ConsumerFactory<String, RoomsSearchVersion> roomsSearchVersionConsumerFactory() {
        JsonDeserializer<RoomsSearchVersion> jsonDeserializer = new JsonDeserializer<>(RoomsSearchVersion.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RoomsSearchVersion> roomsSearchVersionKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RoomsSearchVersion> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(roomsSearchVersionConsumerFactory());
        return factory;
    }
}
