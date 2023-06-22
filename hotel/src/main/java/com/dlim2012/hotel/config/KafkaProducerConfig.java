package com.dlim2012.hotel.config;

import com.dlim2012.clients.dto.IdItem;
import com.dlim2012.clients.dto.hotel.HotelItem;
import com.dlim2012.clients.dto.hotel.RoomItem;
import com.dlim2012.clients.dto.hotel.facility.FacilityItem;
import com.dlim2012.clients.dto.hotel.facility.HotelFacilitiesItem;
import com.dlim2012.clients.dto.hotel.facility.RoomFacilitiesItem;
import com.dlim2012.clients.dto.hotel.facility.RoomFacilityItem;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public Map<String, Object> producerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, RoomItem> roomProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public ProducerFactory<String, HotelItem> hotelProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public ProducerFactory<String, IdItem> idItemProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public ProducerFactory<String, FacilityItem> facilityItemProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public ProducerFactory<String, HotelFacilitiesItem> hotelFacilitiesItemProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public ProducerFactory<String, RoomFacilitiesItem> roomFacilityItemProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, RoomItem> roomKafkaTemplate(
    ) {
        return new KafkaTemplate<>(roomProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, HotelItem> hotelKafkaTemplate(
    ) {
        return new KafkaTemplate<>(hotelProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, IdItem> idItemKafkaTemplate(
    ) {
        return new KafkaTemplate<>(idItemProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, FacilityItem> facilityItemKafkaTemplate(
    ) {
        return new KafkaTemplate<>(facilityItemProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, HotelFacilitiesItem> hotelFacilityItemKafkaTemplate(
    ) {
        return new KafkaTemplate<>(hotelFacilitiesItemProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, RoomFacilitiesItem> roomFacilityItemKafkaTemplate(
    ) {
        return new KafkaTemplate<>(roomFacilityItemProducerFactory());
    }


}
