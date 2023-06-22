package com.dlim2012.clients.kafka.config;

import com.dlim2012.clients.dto.booking.BookingItem;
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
        "com.dlim2012.searchconsumer",
        "com.dlim2012.archival",
        "com.dlim2012.notification"
})
@RequiredArgsConstructor
public class KafkaBookingConsumerConfig{
    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Bean
    public NewTopic bookingTopic(){
        return TopicBuilder.name("booking").build();
    }

    @Bean
    public ConsumerFactory<String, BookingItem> bookingItemConsumerFactory() {
        JsonDeserializer<BookingItem> jsonDeserializer = new JsonDeserializer<>(BookingItem.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BookingItem> bookingItemKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookingItem> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(bookingItemConsumerFactory());
        return factory;
    }
}
