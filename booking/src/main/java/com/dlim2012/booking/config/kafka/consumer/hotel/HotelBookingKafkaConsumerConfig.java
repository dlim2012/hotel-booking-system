package com.dlim2012.booking.config.kafka.consumer.hotel;

import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingDetails;
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
public class HotelBookingKafkaConsumerConfig {
    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Bean
    public NewTopic hotelBookingTopic() {
        return TopicBuilder.name("hotel-booking").build();
    }

    @Bean
    public ConsumerFactory<String, HotelBookingDetails> hotelBookingConsumerFactory() {
        JsonDeserializer<HotelBookingDetails> jsonDeserializer = new JsonDeserializer<>(HotelBookingDetails.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, HotelBookingDetails> hotelBookingKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, HotelBookingDetails> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(hotelBookingConsumerFactory());
        return factory;
    }
}
