package com.dlim2012.hotel.config.kafka.consumer.user;

import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.kafka.dto.user.DeleteUserRequest;
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
public class DeleteUserKafkaConsumerConfig {
    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Bean
    public NewTopic DeleteUserTopic(){
        return TopicBuilder.name("delete-user").build();
    }

    @Bean
    public ConsumerFactory<String, DeleteUserRequest> deleteUserConsumerFactory() {
        JsonDeserializer<DeleteUserRequest> jsonDeserializer = new JsonDeserializer<>(DeleteUserRequest.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeleteUserRequest> deleteUserKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DeleteUserRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deleteUserConsumerFactory());
        return factory;
    }
}
