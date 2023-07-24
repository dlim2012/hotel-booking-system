package com.dlim2012.notification;

import com.dlim2012.clients.advice.ApplicationExceptionHandler;
import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
//@EnableKafka
@Import(value = {
		// advice
		ApplicationExceptionHandler.class,

		// kafka
		KafkaConsumerConfig.class,
})
public class NotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationApplication.class, args);
	}

}
