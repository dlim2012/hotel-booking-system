package com.dlim2012.archival.config.kafka;

import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.kafka.dto.archive.BookingIdArchiveRequest;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;


@Configuration
@EnableKafka
@RequiredArgsConstructor
public class BookingIdArchiveKafkaConsumer {
    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Bean
    public NewTopic bookingArchiveTopic(){
        return TopicBuilder.name("booking-archive").build();
    }

    @Bean
    public ConsumerFactory<String, BookingIdArchiveRequest> bookingIdArchiveConsumerFactory() {
        JsonDeserializer<BookingIdArchiveRequest> jsonDeserializer = new JsonDeserializer<>(BookingIdArchiveRequest.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BookingIdArchiveRequest> bookingIdArchiveListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookingIdArchiveRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(bookingIdArchiveConsumerFactory());
        return factory;
    }
}
