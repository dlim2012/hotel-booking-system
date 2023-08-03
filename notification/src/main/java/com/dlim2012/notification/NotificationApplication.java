package com.dlim2012.notification;

import com.dlim2012.clients.advice.ApplicationExceptionHandler;
import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
//@EnableKafka
@Import(value = {
		// advice
		ApplicationExceptionHandler.class,

		// kafka
		KafkaConsumerConfig.class,
})
@PropertySource("classpath:application-${spring.profiles.active}.yaml")
public class NotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationApplication.class, args);
	}

}
