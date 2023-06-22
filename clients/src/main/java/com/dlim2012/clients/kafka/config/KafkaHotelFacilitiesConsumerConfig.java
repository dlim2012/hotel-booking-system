package com.dlim2012.clients.kafka.config;

import com.dlim2012.clients.dto.hotel.HotelItem;
import com.dlim2012.clients.dto.hotel.facility.HotelFacilitiesItem;
import com.dlim2012.clients.dto.hotel.facility.HotelFacilityItem;
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

import java.util.List;


@Configuration
@EnableKafka
@ComponentScan(basePackages = {
        "com.dlim2012.searchconsumer",
        "com.dlim2012.archival"
})
@RequiredArgsConstructor
public class KafkaHotelFacilitiesConsumerConfig {
    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Bean
    public NewTopic hotelFacilitiesTopic(){
        return TopicBuilder.name("hotel-facilities").build();
    }

    @Bean
    public ConsumerFactory<String, HotelFacilitiesItem> hotelFacilityItemsConsumerFactory() {
        JsonDeserializer<HotelFacilitiesItem> jsonDeserializer = new JsonDeserializer<>(HotelFacilitiesItem.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, HotelFacilitiesItem> hotelFacilitiesItemListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, HotelFacilitiesItem> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(hotelFacilityItemsConsumerFactory());
        return factory;
    }
}
