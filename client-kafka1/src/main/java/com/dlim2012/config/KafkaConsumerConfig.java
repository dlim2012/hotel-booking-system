package com.dlim2012.config;

import com.dlim2012.dto.HotelFullAddressItem;
import com.dlim2012.dto.RoomItem;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@ComponentScan(basePackages = {"com.dlim2012.searchconsumer"})
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public Map<String, Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return props;
    }

    @Bean
    public ConsumerFactory<String, RoomItem> roomItemConsumerFactory() {
        JsonDeserializer<RoomItem> jsonDeserializer = new JsonDeserializer<>(RoomItem.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConsumerFactory<String, HotelFullAddressItem> hotelFullAddressItemConsumerFactory() {
        JsonDeserializer<HotelFullAddressItem> jsonDeserializer = new JsonDeserializer<>(HotelFullAddressItem.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RoomItem> roomItemKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RoomItem> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(roomItemConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, HotelFullAddressItem> hotelFullAddressItemKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, HotelFullAddressItem> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(hotelFullAddressItemConsumerFactory());
        return factory;
    }
}
