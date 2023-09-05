package com.dlim2012.booking.config.kafka.consumer.rooms;

import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingDeleteRequest;
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
        "com.dlim2012.booking"
})
@RequiredArgsConstructor
public class RoomsBookingDeleteKafkaConsumerConfig {
    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Bean
    public NewTopic roomsBookingDeleteTopic() {
        return TopicBuilder.name("rooms-booking-delete").build();
    }

    @Bean
    public ConsumerFactory<String, RoomsBookingDeleteRequest> roomsBookingDeleteConsumerFactory() {
        JsonDeserializer<RoomsBookingDeleteRequest> jsonDeserializer = new JsonDeserializer<>(RoomsBookingDeleteRequest.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RoomsBookingDeleteRequest> roomsBookingDeleteListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RoomsBookingDeleteRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(roomsBookingDeleteConsumerFactory());
        return factory;
    }

}
