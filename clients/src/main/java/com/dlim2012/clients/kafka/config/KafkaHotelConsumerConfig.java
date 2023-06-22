package com.dlim2012.clients.kafka.config;

import com.dlim2012.clients.dto.hotel.HotelItem;
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
        "com.dlim2012.searchconsumer",
        "com.dlim2012.archival"
})
@RequiredArgsConstructor
public class KafkaHotelConsumerConfig {
    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Bean
    public NewTopic hotelTopic(){
        return TopicBuilder.name("hotel").build();
    }

    @Bean
    public ConsumerFactory<String, HotelItem> hotelItemConsumerFactory() {
        JsonDeserializer<HotelItem> jsonDeserializer = new JsonDeserializer<>(HotelItem.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, HotelItem> hotelItemKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, HotelItem> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(hotelItemConsumerFactory());
        return factory;
    }
}
