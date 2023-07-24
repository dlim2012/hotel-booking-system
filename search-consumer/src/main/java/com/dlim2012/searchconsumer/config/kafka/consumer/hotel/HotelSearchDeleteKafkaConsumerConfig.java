package com.dlim2012.searchconsumer.config.kafka.consumer.hotel;

import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.kafka.dto.search.hotel.HotelSearchDeleteRequest;
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
public class HotelSearchDeleteKafkaConsumerConfig {
    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Bean
    public NewTopic hotelSearchDeleteTopic(){
        return TopicBuilder.name("hotel-search-delete").build();
    }

    @Bean
    public ConsumerFactory<String, HotelSearchDeleteRequest> hotelSearchDeleteConsumerFactory() {
        JsonDeserializer<HotelSearchDeleteRequest> jsonDeserializer = new JsonDeserializer<>(HotelSearchDeleteRequest.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, HotelSearchDeleteRequest> hotelSearchDeleteKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, HotelSearchDeleteRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(hotelSearchDeleteConsumerFactory());
        return factory;
    }
}
