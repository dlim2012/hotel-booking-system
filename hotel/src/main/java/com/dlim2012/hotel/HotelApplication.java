package com.dlim2012.hotel;

import com.dlim2012.clients.advice.ApplicationExceptionHandler;
import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.security.config.AuthenticationConfig;
import com.dlim2012.clients.security.config.JwtAuthenticationFilter;
import com.dlim2012.clients.security.config.SecurityConfig;
import com.dlim2012.clients.security.dto.PublicKey;
import com.dlim2012.clients.security.dto.Roles;
import com.dlim2012.clients.security.service.JwtService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.dlim2012.clients")
@EnableKafka
@EnableConfigurationProperties({
        PublicKey.class,
        Roles.class
})
@Import(value = {
        // security
        SecurityConfig.class,
        AuthenticationConfig.class,
        JwtAuthenticationFilter.class,
        JwtService.class,

        //kafka
//        KafkaConsumerConfig.class,
//        KafkaHotelConsumerConfig.class,
//        KafkaRoomConsumerConfig.class,
//        KafkaBookingConsumerConfig.class,

        // advice
        ApplicationExceptionHandler.class
})
public class HotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelApplication.class, args);
    }


//    @Bean
//    CommandLineRunner commandLineRunner(KafkaTemplate<String, Example> kafkaTemplate) {
//        return args -> {
//            for (int i=0; i<100; i++) {
//                kafkaTemplate.send("exampleTopic",
//                        new Example(
//                                "hello kafka :) " + i
////                                LocalDateTime.now()
//                        )
//                );
//            }
//        };
//    }
}
