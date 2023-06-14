package com.dlim2012.hotel;

import com.dlim2012.config.KafkaTopicConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.dlim2012.clients")
@EnableKafka
@Import(value = {KafkaTopicConfig.class})
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
