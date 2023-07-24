package com.dlim2012.searchconsumer.config.kafka.consumer.dates;

import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.kafka.dto.search.dates.DatesUpdateDetails;
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
public class RoomsSearchDatesUpdateKafkaConsumerConfig {
    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Bean
    public NewTopic roomSearchDatesUpdateTopic(){
        return TopicBuilder.name("rooms-search-dates-update").build();
    }

    @Bean
    public ConsumerFactory<String, DatesUpdateDetails> roomsSearchDatesUpdateConsumerFactory() {
        JsonDeserializer<DatesUpdateDetails> jsonDeserializer = new JsonDeserializer<>(DatesUpdateDetails.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DatesUpdateDetails> roomsSearchDatesKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DatesUpdateDetails> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(roomsSearchDatesUpdateConsumerFactory());
        return factory;
    }
}
