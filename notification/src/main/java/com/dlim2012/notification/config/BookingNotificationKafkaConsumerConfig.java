package com.dlim2012.notification.config;

import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingDeleteRequest;
import com.dlim2012.clients.kafka.dto.notification.BookingNotification;
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
        "com.dlim2012.notification"
})
@RequiredArgsConstructor
public class BookingNotificationKafkaConsumerConfig {
    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Bean
    public NewTopic bookingNotificationTopic(){
        return TopicBuilder.name("booking-notification").build();
    }

    @Bean
    public ConsumerFactory<String, BookingNotification> bookingNotificationConsumerFactory() {
        JsonDeserializer<BookingNotification> jsonDeserializer = new JsonDeserializer<>(BookingNotification.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BookingNotification> bookingNotificationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookingNotification> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(bookingNotificationConsumerFactory());
        return factory;
    }
}
