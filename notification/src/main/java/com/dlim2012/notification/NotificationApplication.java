package com.dlim2012.notification;

import com.dlim2012.clients.advice.ApplicationExceptionHandler;
import com.dlim2012.clients.kafka.config.KafkaBookingConsumerConfig;
import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.kafka.config.KafkaHotelConsumerConfig;
import com.dlim2012.clients.kafka.config.KafkaRoomConsumerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
//@EnableKafka
@Import(value = {
		// advice
		ApplicationExceptionHandler.class,

		// kafka
//		KafkaConsumerConfig.class,
//		KafkaRoomConsumerConfig.class,
//		KafkaBookingConsumerConfig.class
})
public class NotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationApplication.class, args);
	}

}
