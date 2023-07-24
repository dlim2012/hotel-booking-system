package com.dlim2012.notification.config;

import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.kafka.dto.notification.BookingNotification;
import com.dlim2012.clients.kafka.dto.notification.PaymentNotification;
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
public class PaymentNotificationKafkaConsumerConfig {
    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Bean
    public NewTopic paymentNotificationTopic(){
        return TopicBuilder.name("payment-notification").build();
    }

    @Bean
    public ConsumerFactory<String, PaymentNotification> paymentNotificationConsumerFactory() {
        JsonDeserializer<PaymentNotification> jsonDeserializer = new JsonDeserializer<>(PaymentNotification.class);
        jsonDeserializer.addTrustedPackages("com.dlim2012");
        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.consumerConfig(),
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentNotification> paymentNotificationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentNotification> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentNotificationConsumerFactory());
        return factory;
    }
}
